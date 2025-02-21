package neo.spider.solution.flowcontrol;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.resilience4j.ratelimiter.RateLimiter;

import java.util.concurrent.TimeUnit;

public class UserRateLimiterRegistry {

    private static volatile UserRateLimiterRegistry instance;
    private final Cache<String, RateLimiter> cache;

    private UserRateLimiterRegistry() {
        this.cache = Caffeine.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build();
    }

    public static UserRateLimiterRegistry getInstance() {
        if (instance == null) {
            synchronized (UserRateLimiterRegistry.class) {
                if (instance == null) {
                    instance = new UserRateLimiterRegistry();
                }
            }
        }
        return instance;
    }

    public Cache<String, RateLimiter> getCache() {
        return cache;
    }
}
