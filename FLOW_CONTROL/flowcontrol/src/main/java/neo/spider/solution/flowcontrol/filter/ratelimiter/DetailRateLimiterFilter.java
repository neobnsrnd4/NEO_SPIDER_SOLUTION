package neo.spider.solution.flowcontrol.filter.ratelimiter;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import neo.spider.solution.flowcontrol.ConfigurationProp;
import neo.spider.solution.flowcontrol.trie.TrieRegistry;

import java.io.IOException;

public class DetailRateLimiterFilter implements Filter {

	private final FilterManager filterManager;
	private final ConfigurationProp prop;
	private final String groupName;
	private final RateLimiterRegistry rateLimiterRegistry;
	private final TrieRegistry trieRegistry;

	public DetailRateLimiterFilter(FilterManager filterManager, ConfigurationProp prop,
			RateLimiterRegistry rateLimiterRegistry, TrieRegistry trieRegistry) {
		this.filterManager = filterManager;
		this.prop = prop;
		this.groupName = prop.getFilters().getGroup1();
		this.rateLimiterRegistry = rateLimiterRegistry;
		this.trieRegistry = trieRegistry;

	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {

		// 필터 그룹 비활성화 시 실행 안함
		if (!filterManager.isGroupEnabled(groupName)) {
			filterChain.doFilter(servletRequest, servletResponse);
			System.out.println("resilience4j detail rate limiter 실행 안함");
			return;
		}
		
		System.out.println("resilinece detail rate limiter 실행");

		HttpServletRequest request = (HttpServletRequest) servletRequest;
		String uri = request.getRequestURI();

		String name = trieRegistry.getRateLimiterTrie().search(uri);

		if (name == null) {
			filterChain.doFilter(servletRequest, servletResponse);
		} else {
			RateLimiter rl = rateLimiterRegistry.rateLimiter(name);
			if (rl.acquirePermission()) {
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
}
