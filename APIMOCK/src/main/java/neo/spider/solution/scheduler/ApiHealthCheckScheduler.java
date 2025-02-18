package neo.spider.solution.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import neo.spider.solution.apimock.service.ApiService;

@Component
@RequiredArgsConstructor
public class ApiHealthCheckScheduler {

	private final ApiService apiService;
	
	@Scheduled(fixedRate = 300000)//5분 간격
	public synchronized void performScheduledHealthCheck() {
		apiService.checkAllApiHealthCheck();
	}
	
}
