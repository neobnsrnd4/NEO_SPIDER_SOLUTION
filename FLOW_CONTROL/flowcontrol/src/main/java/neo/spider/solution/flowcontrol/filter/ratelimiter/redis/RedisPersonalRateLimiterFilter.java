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

public class RedisPersonalRateLimiterFilter implements Filter {

	private final FilterManager filterManager;
	private final ConfigurationProp prop;
	private final String groupName;
	private final JwtProviderService jwtProviderService;
	private final RedisRateLimiterService redisRateLimiterService;
	private final RedisService redisService;

	@Autowired
	public RedisPersonalRateLimiterFilter(FilterManager filterManager, ConfigurationProp prop,
			JwtProviderService jwtProviderService, RedisRateLimiterService redisRateLimiterService,
			RedisService redisService) {
		this.filterManager = filterManager;
		this.prop = prop;
		this.groupName = prop.getFilters().getGroup2();
		this.jwtProviderService = jwtProviderService;
		this.redisRateLimiterService = redisRateLimiterService;
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
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		// jwt 토큰 확인. 쿠키 변경은 막으나 삭제 후 사용을 막기 위해 로그인과 연동
		String token = null;

		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if ("X-User-JWT".equals(cookie.getName())) {
					token = cookie.getValue().trim();
				}
			}
		}

		if (token == null || token == "") {
			token = jwtProviderService.generateToken();

			Cookie cookie = new Cookie("X-User-JWT", token);

			response.addCookie(cookie);
			System.out.println("jwt 신규 발행. token : " + token);
		} else {
			try {
				Claims claims = jwtProviderService.parseToken(token);
			} catch (Exception e) {
				// 인증되지 않은 사용자
				System.out.println("jwt 인증 실패 사용자 : " + e);
				HttpServletResponse httpServletResponse = sendJwtAuthenticationFailResponse(servletResponse);
				return;
			}
			System.out.println("jwt 인증 성공 사용자. token : " + token);
		}

		HttpSession session = request.getSession(true);

		String applicationName = prop.getApplication().getName();
		// personal type: 2
		String key = applicationName + "/2/personal/" + token;
		Bucket bucket = (Bucket) session.getAttribute(key);

		// bucket 기본 설정 및 소비
		Long sessionCapacity = Long.parseLong(
				session.getAttribute("capacity/2") == null ? "0" : session.getAttribute("capacity/2").toString());
		Long sessionRefill = Long.parseLong(
				session.getAttribute("refill/2") == null ? "0" : session.getAttribute("refill/2").toString());

		long capacity = Long.parseLong(redisService.getStringValue(applicationName + "/capacity/2/personal"));
		long refill = Long.parseLong(redisService.getStringValue(applicationName + "/refill/2/personal"));

		System.out.println("// " + sessionCapacity + " : " + sessionRefill);

		if ((sessionCapacity == 0 && sessionRefill == 0) || (sessionCapacity == capacity && sessionRefill == refill)) {
			// 이전과 같거나 최초
			session.setAttribute("capacity/2", capacity);
			session.setAttribute("refill/2", refill);
			bucket = redisRateLimiterService.getBucket(key, capacity, refill);
		} else {
			// configuration replacement
			System.out.println("// configuration replacement" + sessionCapacity + " : " + sessionRefill);
			session.setAttribute("capacity/2", capacity);
			session.setAttribute("refill/2", refill);
			bucket = redisRateLimiterService.getReplacedBucket(key, capacity, refill);
		}

		System.out.println(capacity + "personal : capa & refill" + refill);

		ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
		System.out.println("personal key: " + key);
		System.out.println("personal probe : " + probe.toString());

		if (probe.isConsumed()) {
			filterChain.doFilter(servletRequest, servletResponse);
			System.out.println("personal consumed : " + probe.toString());
		} else {
			// fail
			HttpServletResponse httpServletResponse = sendErrorResponse(servletResponse, probe);
			System.out.println("personal failed : " + probe.toString());
		}
	}

	private HttpServletResponse sendErrorResponse(ServletResponse servletResponse, ConsumptionProbe probe)
			throws IOException {

		String retryTime = "" + TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill());

		HttpServletResponse response = (HttpServletResponse) servletResponse;
		response.setContentType("text/plain");
		response.setHeader("X-Rate-Limit-Retry-After-Seconds", retryTime);
		response.setStatus(500);
		response.getWriter().append("Too Many Request for one user. Wait For " + retryTime + " seconds!");

		return response;

	}

	private HttpServletResponse sendJwtAuthenticationFailResponse(ServletResponse servletResponse) throws IOException {
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		response.setContentType("text/plain");
		response.setHeader("X-Jwt-Fail", "Failed to Authenticate.");
		response.setStatus(500);
		response.getWriter().append("Failed to Authenticate");
		return response;
	}

}
