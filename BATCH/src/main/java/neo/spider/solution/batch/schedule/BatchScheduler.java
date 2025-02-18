package neo.spider.solution.batch.schedule;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BatchScheduler {

	private final JobLauncher jobLauncher;
	private final JobRegistry jobRegistry;

	@Scheduled(cron = "0 0 0 * * * ", zone = "Asia/Seoul")
	public String runApijobSchedule() throws Exception {

		String value = "" + (int) Math.random() * 10;

		String uniqVal = value + System.currentTimeMillis();

		JobParameters jobParameters = new JobParametersBuilder().addString("dbtoapi", uniqVal).toJobParameters();

		try {
			jobLauncher.run(jobRegistry.getJob("dbToApiJob"), jobParameters);
		} catch (Exception e) {
			return "FAIL";
		}

		return "OK";
	}

	@Scheduled(cron = "0 0 0 * * * ", zone = "Asia/Seoul")
	public String runCopyjobSchedule() throws Exception {

		String value = "" + (int) Math.random() * 10;

		String uniqVal = value + System.currentTimeMillis();

		JobParameters jobParameters = new JobParametersBuilder().addString("dbtodb", uniqVal).toJobParameters();

		try {
			jobLauncher.run(jobRegistry.getJob("dbCopyJob"), jobParameters);
		} catch (Exception e) {
			return "FAIL";
		}

		return "OK";
	}

	@Scheduled(cron = "0 0 0 * * * ", zone = "Asia/Seoul")
	public String runFilejobSchedule() throws Exception {

		String value = "" + (int) Math.random() * 10;

		String uniqVal = value + System.currentTimeMillis();

		JobParameters jobParameters = new JobParametersBuilder().addString("filetodb", uniqVal).toJobParameters();

		try {
			jobLauncher.run(jobRegistry.getJob("fileToDBJob"), jobParameters);
			return "OK";
		} catch (Exception e) {
			e.printStackTrace();
			return "FAIL";
		}
	}

	@Scheduled(cron = "0 0 0 * * * ", zone = "Asia/Seoul")
	public String runParentjobSchedule() throws Exception {

		String value = "" + (int) Math.random() * 10;

		String uniqVal = value + System.currentTimeMillis();

		JobParameters jobParameters = new JobParametersBuilder().addString("parentBatch", uniqVal).toJobParameters();

		try {
			jobLauncher.run(jobRegistry.getJob("parentBatchJob"), jobParameters);
			return "OK";
		} catch (Exception e) {
			return "FAIL";
		}
	}

}
