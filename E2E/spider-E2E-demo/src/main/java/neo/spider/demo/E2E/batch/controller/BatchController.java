package neo.spider.demo.E2E.batch.controller;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import neo.spider.demo.E2E.batch.service.FileMaintenanceService;

@RestController
@RequiredArgsConstructor
public class BatchController {
   private final JobLauncher jobLauncher;
   private final JobRegistry jobRegistry;
   private String rolledFilesPath = "../ss/logs/rolling";

   @GetMapping("/batch/logtodb/{value}")
	public String logToDbTest(@PathVariable("value") String value) {

		String uniqVal = value + System.currentTimeMillis();

		JobParameters jobParameters = new JobParametersBuilder().addString("logtodb", uniqVal).toJobParameters();

		try {
			JobExecution jobExecution = jobLauncher.run(jobRegistry.getJob("logToDBJob"), jobParameters);

			while (jobExecution.isRunning()) {
				Thread.sleep(500);
			}
			
			if (jobExecution.getStatus() == BatchStatus.FAILED) {
				return "FAIL";
			}
			
			// 폴더 하위 전체 파일 삭제
			FileMaintenanceService fileMaintenanceService = new FileMaintenanceService();		
			fileMaintenanceService.cleanupLogFolder(rolledFilesPath);

			return "OK";
			
		} catch (Exception e) {
			e.printStackTrace();
			return "FAIL";
		}
	}

}
