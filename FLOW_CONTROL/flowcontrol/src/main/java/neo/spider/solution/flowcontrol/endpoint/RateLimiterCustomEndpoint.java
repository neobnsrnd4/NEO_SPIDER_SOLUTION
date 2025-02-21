package neo.spider.solution.flowcontrol.endpoint;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.stereotype.Component;
import neo.spider.solution.flowcontrol.ConfigurationProp;

import java.util.HashMap;
import java.util.Map;

@Component
@Endpoint(id = "ratelimiter")
public class RateLimiterCustomEndpoint {

    private final RateLimiterRegistry registry;
    private final ConfigurationProp prop;

    public RateLimiterCustomEndpoint(RateLimiterRegistry registry, ConfigurationProp prop) {
        this.registry = registry;
        this.prop = prop;
    }

    public Map<String, Object> rateLimiter(){
        Map<String, Object> results = new HashMap<>();
        Map<String, Integer> tokens = new HashMap<>();
        for (RateLimiter rateLimiter : registry.getAllRateLimiters()) {
            tokens.put(rateLimiter.getName(), rateLimiter.getMetrics().getAvailablePermissions());
        }
        results.put("application", prop.getName());
        results.put("available", tokens);
        return results;
    }
}
