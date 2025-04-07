package neo.spider.solution.flowcontrol.config;

import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimiterConfig {

    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
            return RateLimiterRegistry.of(io.github.resilience4j.ratelimiter.RateLimiterConfig.custom()
                            .limitForPeriod(50)
                            .limitRefreshPeriod(java.time.Duration.ofSeconds(1))
                            .timeoutDuration(java.time.Duration.ofSeconds(5))
                    .build());
    }
}