package neo.spider.solution.batch.schedule;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import neo.spider.solution.batch.service.FileMaintenanceService;

@Component
@RequiredArgsConstructor
public class BatchScheduler {

	private final JobLauncher jobLauncher;
	private final JobRegistry jobRegistry;
	private String rolledFilesPath = "../spider/logs/rolling";

	@Scheduled(cron = "0 0 0 * * * ", zone = "Asia/Seoul")
	public String runApijobSchedule() throws Exception {

		String value = "" + (int) Math.random() * 10;

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

	@Scheduled(cron = "0 0 0 * * * ", zone = "Asia/Seoul")
	public String runCopyjobSchedule() throws Exception {

		String value = "" + (int) Math.random() * 10;

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

	@Scheduled(cron = "0 0 0 * * * ", zone = "Asia/Seoul")
	public String runFilejobSchedule() throws Exception {

		String value = "" + (int) Math.random() * 10;

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

	@Scheduled(cron = "0 0 0 * * * ", zone = "Asia/Seoul")
	public String runParentjobSchedule() throws Exception {

		String value = "" + (int) Math.random() * 10;

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

	@Scheduled(cron = "0 0 0 * * * ", zone = "Asia/Seoul")
	public String runLogToDbjobSchedule() throws Exception {

		String value = "" + (int) Math.random() * 10;

		String uniqVal = value + System.currentTimeMillis();

		JobParameters jobParameters = new JobParametersBuilder().addString("logtodb", uniqVal).toJobParameters();

		try {
			JobExecution jobExecution = jobLauncher.run(jobRegistry.getJob("logToDBJob"), jobParameters);

			while (jobExecution.isRunning()) {
				Thread.sleep(500);
			}
			// 폴더 하위 전체 파일 삭제
			FileMaintenanceService fileMaintenanceService = new FileMaintenanceService();
			fileMaintenanceService.cleanupLogFolder(rolledFilesPath);

			if (jobExecution.getStatus() == BatchStatus.FAILED) {
				return "FAIL";
			}

			return "OK";

		} catch (Exception e) {
			e.printStackTrace();
			return "FAIL";
		}

	}

}
