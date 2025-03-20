package neo.spider.solution.flowcontrol.filter.ratelimiter.redis;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.quartz.QuartzEndpoint.GroupNamesDescriptor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import neo.spider.solution.flowcontrol.ConfigurationProp;
import neo.spider.solution.flowcontrol.filter.ratelimiter.FilterManager;
import neo.spider.solution.flowcontrol.service.RedisRateLimiterService;
import neo.spider.solution.flowcontrol.service.RedisService;

public class RedisPersonalRateLimiterFilter implements Filter {

	private final FilterManager filterManager;
	private final ConfigurationProp prop;
	private final String groupName;
	private final RedisRateLimiterService redisRateLimiterService;
	private final RedisService redisService;

	@Autowired
	public RedisPersonalRateLimiterFilter(FilterManager filterManager, ConfigurationProp prop,
			RedisRateLimiterService bucketRateLimiterService, RedisService redisService) {
		this.filterManager = filterManager;
		this.prop = prop;
		this.groupName = prop.getFilters().getGroup2();
		this.redisRateLimiterService = bucketRateLimiterService;
		this.redisService = redisService;

	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {

		// 필터 그룹 비활성화 시 실행 안함
		if (!filterManager.isGroupEnabled(groupName)) {
			filterChain.doFilter(servletRequest, servletResponse);
			System.out.println("redis personal rate limiter 실행 안함");
			return;
		}
		
		System.out.println("redis personal rate limiter 실행");

		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpSession session = request.getSession(true);
		String key = session.getId() + "/" + request.getRequestURI();
		Bucket bucket = (Bucket) session.getAttribute(key);

		// bucket 기본 설정 및 소비
		String applicationName = prop.getApplication().getName();

		Long sessionCapacity = Long.parseLong(
				session.getAttribute("capacity") == null ? "0" : session.getAttribute("capacity").toString());
		Long sessionRefill = Long
				.parseLong(session.getAttribute("refill") == null ? "0" : session.getAttribute("refill").toString());

		long capacity = Long.parseLong(redisService.getStringValue(applicationName + "/capacity"));
		long refill = Long.parseLong(redisService.getStringValue(applicationName + "/refill"));

		System.out.println("// " + sessionCapacity + " : " + sessionRefill);

		if ((sessionCapacity == 0 && sessionRefill == 0) || (sessionCapacity == capacity && sessionRefill == refill)) {
			// 이전과 같거나 최초
			session.setAttribute("capacity", capacity);
			session.setAttribute("refill", refill);
			bucket = redisRateLimiterService.getBucket(key, capacity, refill);
		} else {
			// configuration replacement
			System.out.println("// configuration replacement" + sessionCapacity + " : " + sessionRefill);
			session.setAttribute("capacity", capacity);
			session.setAttribute("refill", refill);
			bucket = redisRateLimiterService.getReplacedBucket(key, capacity, refill);
		}

		System.out.println(capacity + " capa & refill" + refill);

		ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
		System.out.println("key: " + key);
		System.out.println("probe : " + probe.toString());

		if (probe.isConsumed()) {
			filterChain.doFilter(servletRequest, servletResponse);
			System.out.println("consumed : " + probe.toString());
		} else {
			// fail
			HttpServletResponse httpServletResponse = sendErrorResponse(servletResponse, probe);
			System.out.println("failed : " + probe.toString());
		}
	}

	private HttpServletResponse sendErrorResponse(ServletResponse servletResponse, ConsumptionProbe probe)
			throws IOException {

		String retryTime = "" + TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill());

		HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
		httpServletResponse.setContentType("text/plain");
		httpServletResponse.setHeader("X-Rate-Limit-Retry-After-Seconds", retryTime);
		httpServletResponse.setStatus(500);
		httpServletResponse.getWriter().append("Too Many Request. Wait For " + retryTime + " seconds!");

		return httpServletResponse;

	}

}
