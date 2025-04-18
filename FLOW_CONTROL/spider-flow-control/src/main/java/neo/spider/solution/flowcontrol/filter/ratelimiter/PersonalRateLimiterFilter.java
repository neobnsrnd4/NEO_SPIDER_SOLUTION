package neo.spider.solution.flowcontrol.filter.ratelimiter;

import com.github.benmanes.caffeine.cache.Cache;
import jakarta.servlet.http.HttpServletResponse;
import neo.spider.solution.flowcontrol.ConfigurationProp;
import neo.spider.solution.flowcontrol.UserRateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public class PersonalRateLimiterFilter implements Filter {

	private final FilterManager filterManager;
	private final ConfigurationProp prop;
	private final String groupName;
	private final RateLimiterRegistry rateLimiterRegistry;

	public PersonalRateLimiterFilter(FilterManager filterManager, ConfigurationProp prop,
			RateLimiterRegistry rateLimiterRegistry) {
		this.filterManager = filterManager;
		this.prop = prop;
		this.groupName = prop.getFilters().getGroup1();
		this.rateLimiterRegistry = rateLimiterRegistry;
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {

		// 필터 그룹 비활성화 시 실행 안함
		if (!filterManager.isGroupEnabled(groupName)) {
			filterChain.doFilter(servletRequest, servletResponse);
			System.out.println("resilience4j personal rate limiter 실행 안함");
			return;
		}
		
		System.out.println("resilinece personal rate limiter 실행");

		HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
		String id = httpRequest.getHeader("id");

		Cache<String, RateLimiter> cache = UserRateLimiterRegistry.getInstance().getCache();
		if (id == null) {
			filterChain.doFilter(servletRequest, servletResponse);
		} else {
			if (rateLimiterRegistry.find("personal").isPresent()) {
				RateLimiter userRateLimiter = cache.getIfPresent(id);
				if (userRateLimiter == null) {
					RateLimiter rateLimiter = rateLimiterRegistry.find("personal").get();
					RateLimiterConfig config = rateLimiter.getRateLimiterConfig();
					userRateLimiter = RateLimiter.of(id, config);
					cache.put(id, userRateLimiter);
				}

				if (userRateLimiter.acquirePermission()) {
					filterChain.doFilter(servletRequest, servletResponse);
				} else {
					HttpServletResponse resp = (HttpServletResponse) servletResponse;
					resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					resp.setContentType("application/json");
					resp.setCharacterEncoding("UTF-8");
					resp.getWriter().write("{ \"error\": \"Request not permitted. Please try again later.\" }");
					resp.getWriter().flush();
				}
			} else {
				filterChain.doFilter(servletRequest, servletResponse);
			}
		}
	}
}
