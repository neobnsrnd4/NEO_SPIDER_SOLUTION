package neo.spider.demo.E2E.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DemoFilter extends OncePerRequestFilter{

	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		logger.info("프로젝트에서 재정의한 필터가 동작함.");
		
		filterChain.doFilter(request, response);
		
	}

}
