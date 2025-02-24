package neo.spider.solution.batch.batchJobs;

import org.slf4j.MDC;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.stereotype.Component;

import neo.spider.solution.batch.service.BatchHistoryService;

@Component 
public class CustomBatchJobListener{
	
	private final BatchHistoryService service;

	public CustomBatchJobListener(BatchHistoryService service) {
		super();
		this.service = service;
	}
	
	// 배치 잡 이름, 아이디를 히스토리 내역 테이블에 저장
	@AfterJob
	public void afterJob(JobExecution jobExecution) {
		String jobName = jobExecution.getJobInstance().getJobName().toUpperCase();
		MDC.put("batchAppId", "NEO.SPIDER.SOLUTION.BATCH." + jobName);
        MDC.put("instanceId", String.valueOf("BH" + jobExecution.getJobInstance().getInstanceId()));
		service.saveBatchHistory(jobExecution);
		MDC.remove("batchAppId");
		MDC.remove("instanceId");
	}

}
