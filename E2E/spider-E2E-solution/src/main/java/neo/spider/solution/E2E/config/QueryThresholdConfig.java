package neo.spider.solution.E2E.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class QueryThresholdConfig {
	
	 // Static 변수로 설정값을 유지
    private static long queryThresholdMs;

    @Value("${query.executed.time.threshold:500}") // 기본값 500ms
    public void setQueryThresholdMs(long queryThresholdMs) {
        QueryThresholdConfig.queryThresholdMs = queryThresholdMs;
    }
    
    public static long getQueryThresholdMs() {
    	return queryThresholdMs;
    }
}
