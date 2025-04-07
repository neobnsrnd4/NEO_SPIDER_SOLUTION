package neo.spider.solution.flowcontrol.config;

import io.github.resilience4j.bulkhead.BulkheadRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class BulkheadConfig {

    @Bean
    public BulkheadRegistry bulkheadRegistry() {
        return BulkheadRegistry.of(io.github.resilience4j.bulkhead.BulkheadConfig.custom()
                .maxConcurrentCalls(50)
                        .maxWaitDuration(Duration.ofSeconds(5))
                .build());
    }
}
