//package neo.spider.demo.E2E.batch.schedule;
//
//
//import org.springframework.batch.core.JobParameters;
//import org.springframework.batch.core.JobParametersBuilder;
//import org.springframework.batch.core.configuration.JobRegistry;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import lombok.RequiredArgsConstructor;
//
//@Component
//@RequiredArgsConstructor
//public class BatchScheduler {
//
//	private final JobLauncher jobLauncher;
//	private final JobRegistry jobRegistry;
//
//	@Scheduled(cron = "0 0 0 * * * ", zone = "Asia/Seoul")
//	public String runLogFilejobSchedule() throws Exception {
//
//		String uniqVal = "logToDb" + System.currentTimeMillis();
//		
//		JobParameters jobParameters = new JobParametersBuilder().addString("logtodb", uniqVal)
//				.toJobParameters();
//
//		try {
//			jobLauncher.run(jobRegistry.getJob("logToDBJob"), jobParameters);
//			return "OK";
//		} catch (Exception e) {
//			e.printStackTrace();
//			return "FAIL";
//		}
//	}
//
//}


