package neo.spider.solution.flowcontrol.filter.ratelimiter.redis;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.jsonwebtoken.Claims;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import neo.spider.solution.flowcontrol.ConfigurationProp;
import neo.spider.solution.flowcontrol.filter.ratelimiter.FilterManager;
import neo.spider.solution.flowcontrol.service.JwtProviderService;
import neo.spider.solution.flowcontrol.service.RedisRateLimiterService;
import neo.spider.solution.flowcontrol.service.RedisService;

public class RedisGlobalRateLimiterFilter implements Filter {

	private final FilterManager filterManager;
	private final ConfigurationProp prop;
	private final String groupName;
	private final RedisRateLimiterService redisRateLimiterService;
	private final RedisService redisService;

	@Autowired
	public RedisGlobalRateLimiterFilter(FilterManager filterManager, ConfigurationProp prop,
			RedisRateLimiterService redisRateLimiterService, RedisService redisService) {
		this.filterManager = filterManager;
		this.prop = prop;
		this.groupName = prop.getFilters().getGroup2();
		this.redisRateLimiterService = redisRateLimiterService;
		this.redisService = redisService;

	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {

		// 필터 그룹 비활성화 시 실행 안함
		if (!filterManager.isGroupEnabled(groupName)) {
			filterChain.doFilter(servletRequest, servletResponse);
			System.out.println("redis global rate limiter 실행 안함");
			return;
		}

		System.out.println("redis global rate limiter 실행");

		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		HttpSession session = request.getSession(true);

		String applicationName = prop.getApplication().getName();
		// global type: 0
		String key = applicationName + "/0/global";
		Bucket bucket = (Bucket) session.getAttribute(key);

		// bucket 기본 설정 및 소비
		Long sessionCapacity = Long.parseLong(
				session.getAttribute("capacity/0") == null ? "0" : session.getAttribute("capacity/0").toString());
		Long sessionRefill = Long.parseLong(
				session.getAttribute("refill/0") == null ? "0" : session.getAttribute("refill/0").toString());

		long capacity = Long.parseLong(redisService.getStringValue(applicationName + "/capacity/0/global"));
		long refill = Long.parseLong(redisService.getStringValue(applicationName + "/refill/0/global"));

		System.out.println("// " + sessionCapacity + " : " + sessionRefill);

		if ((sessionCapacity == 0 && sessionRefill == 0) || (sessionCapacity == capacity && sessionRefill == refill)) {
			// 이전과 같거나 최초
			session.setAttribute("capacity/0", capacity);
			session.setAttribute("refill/0", refill);
			bucket = redisRateLimiterService.getBucket(key, capacity, refill);
		} else {
			// configuration replacement
			System.out.println("// configuration replacement" + sessionCapacity + " : " + sessionRefill);
			session.setAttribute("capacity/0", capacity);
			session.setAttribute("refill/0", refill);
			bucket = redisRateLimiterService.getReplacedBucket(key, capacity, refill);
		}

		System.out.println(capacity + "global : capa & refill" + refill);

		ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
		System.out.println("global key: " + key);
		System.out.println("global probe : " + probe.toString());

		if (probe.isConsumed()) {
			filterChain.doFilter(servletRequest, servletResponse);
			System.out.println("global consumed : " + probe.toString());
		} else {
			// fail
			HttpServletResponse httpServletResponse = sendErrorResponse(servletResponse, probe);
			System.out.println("global failed : " + probe.toString());
		}
	}

	private HttpServletResponse sendErrorResponse(ServletResponse servletResponse, ConsumptionProbe probe)
			throws IOException {

		String retryTime = "" + TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill());

		HttpServletResponse response = (HttpServletResponse) servletResponse;
		response.setContentType("text/plain");
		response.setHeader("X-Rate-Limit-Retry-After-Seconds", retryTime);
		response.setStatus(500);
		response.getWriter().append("Too Many Request For this application instance. Wait For " + retryTime + " seconds!");

		return response;

	}

}
