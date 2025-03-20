package neo.spider.solution.flowcontrol.filter.ratelimiter;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import neo.spider.solution.flowcontrol.ConfigurationProp;

import java.io.IOException;

public class GlobalRateLimiterFilter implements Filter {

	private final FilterManager filterManager;
	private final ConfigurationProp prop;
	private final String groupName;
	private final RateLimiterRegistry rateLimiterRegistry;

	public GlobalRateLimiterFilter(FilterManager filterManager, ConfigurationProp prop,
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
			System.out.println("resilience4j global rate limiter 실행 안함");
			return;
		}
		
		System.out.println("resilinece global rate limiter 실행");

		boolean isPresent = rateLimiterRegistry.find("global").isPresent();
		if (!isPresent) {
			filterChain.doFilter(servletRequest, servletResponse);
		} else {
			RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("global");

			if (rateLimiter.acquirePermission()) {
				filterChain.doFilter(servletRequest, servletResponse);
			} else {
				HttpServletResponse resp = (HttpServletResponse) servletResponse;
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				resp.setContentType("application/json");
				resp.setCharacterEncoding("UTF-8");
				resp.getWriter().write("{ \"error\": \"Request not permitted. Please try again later.\" }");
				resp.getWriter().flush();
			}
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		Filter.super.init(filterConfig);
	}

	@Override
	public void destroy() {
		Filter.super.destroy();
	}
}
