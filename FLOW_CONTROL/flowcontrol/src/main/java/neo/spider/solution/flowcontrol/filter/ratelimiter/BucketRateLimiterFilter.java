package neo.spider.solution.flowcontrol.filter.ratelimiter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class BucketRateLimiterFilter implements Filter {

	private ProxyManager<String> proxyManager;

	@Autowired
	public BucketRateLimiterFilter(ProxyManager<String> proxyManager) {
		this.proxyManager = proxyManager;
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		String key = request.getRequestURI();

		Supplier<BucketConfiguration> bucketSupplier = getConfigSupplier();
		Bucket bucket = proxyManager.builder().build(key, bucketSupplier);
		ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

		if (probe.isConsumed()) {
			filterChain.doFilter(servletRequest, servletResponse);
		} else {
			// fail
			HttpServletResponse httpServletResponse = sendErrorResponse(servletResponse, probe);
		}

	}

	public Supplier<BucketConfiguration> getConfigSupplier() {
		return () -> BucketConfiguration.builder()
				.addLimit(limit -> limit.capacity(5).refillGreedy(5, Duration.ofMinutes(3))).build();
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
