package neo.spider.solution.E2E.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RestTemplateThresholdConfig {
	
	 // Static 변수로 설정값을 유지
    private static long restTemplateThresholdMs;

    @Value("${rest-template.executed.time.threshold:500}") // 기본값 500ms
    public void setQueryThresholdMs(long restTemplateThresholdMs) {
        RestTemplateThresholdConfig.restTemplateThresholdMs = restTemplateThresholdMs;
    }
    
    public static long getRestTemplateThresholdMs() {
    	return restTemplateThresholdMs;
    }
}
