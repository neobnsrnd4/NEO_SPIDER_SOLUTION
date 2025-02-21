package neo.spider.solution.batch.controller;

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
import neo.spider.solution.batch.service.FileMaintenanceService;

@RestController
@RequiredArgsConstructor
public class BatchController {
	private final JobLauncher jobLauncher;
	private final JobRegistry jobRegistry;
	private String rolledFilesPath = "../spider/logs/rolling";

	@GetMapping("/batch/dbtoapi/{value}")
	public String firstApi(@PathVariable("value") String value) throws Exception {

		String uniqVal = value + System.currentTimeMillis();

		JobParameters jobParameters = new JobParametersBuilder().addString("dbtoapi", uniqVal).toJobParameters();

		// document The JobExecution Response: JobExecution이 성공적으로 생성만 되면 항상 반환됨.
		// TaskExecutor가 멀티스레드 일때는 바로 돌아오고 싱글스레드일 때는 잡이 끝나야 리턴

		try {
			JobExecution jobExecution = jobLauncher.run(jobRegistry.getJob("dbToApiJob"), jobParameters);

			while (jobExecution.isRunning()) {
				Thread.sleep(500);
			}

			if (jobExecution.getStatus() == BatchStatus.FAILED) {
				return "FAIL";
			}

			return "OK";

		} catch (Exception e) {
			return "FAIL";
		}

	}

	@GetMapping("/batch/dbtodb/{value}")
	public String dbtodbTest(@PathVariable("value") String value) throws Exception {

		String uniqVal = value + System.currentTimeMillis();

		JobParameters jobParameters = new JobParametersBuilder().addString("dbtodb", uniqVal).toJobParameters();

		try {
			JobExecution jobExecution = jobLauncher.run(jobRegistry.getJob("dbCopyJob"), jobParameters);

			while (jobExecution.isRunning()) {
				Thread.sleep(500);
			}

			if (jobExecution.getStatus() == BatchStatus.FAILED) {
				return "FAIL";
			}

			return "OK";
		} catch (Exception e) {
			return "FAIL";
		}

	}

	@GetMapping("/batch/filetodb/{value}")
	public String fileToDbTest(@PathVariable("value") String value) {

		String uniqVal = value + System.currentTimeMillis();

		JobParameters jobParameters = new JobParametersBuilder().addString("filetodb", uniqVal).toJobParameters();

		try {
			JobExecution jobExecution = jobLauncher.run(jobRegistry.getJob("fileToDBJob"), jobParameters);

			while (jobExecution.isRunning()) {
				Thread.sleep(500);
			}

			if (jobExecution.getStatus() == BatchStatus.FAILED) {
				return "FAIL";
			}

			return "OK";
		} catch (Exception e) {
			return "FAIL";
		}
	}

	@GetMapping("/batch/parentbatch/{value}")
	public String parentBatchTest(@PathVariable("value") String value) {

		String uniqVal = value + System.currentTimeMillis();

		JobParameters jobParameters = new JobParametersBuilder().addString("parentBatch", uniqVal).toJobParameters();

		try {
			JobExecution jobExecution = jobLauncher.run(jobRegistry.getJob("parentBatchJob"), jobParameters);
			while (jobExecution.isRunning()) {
				Thread.sleep(500);
			}

			if (jobExecution.getStatus() == BatchStatus.FAILED) {
				return "FAIL";
			}

			return "OK";
		} catch (Exception e) {
			return "FAIL";
		}
	}

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
