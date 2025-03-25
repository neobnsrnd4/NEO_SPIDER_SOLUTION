package neo.spider.demo.E2E.batch;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import neo.spider.demo.E2E.batch.service.LogFileToDbService;

@Configuration
public class LogFileToDbBatch {

   private final JobRepository jobRepository;
   private final PlatformTransactionManager transactionManager;
   private LogFileToDbService logFileToDbService;
   private String path = "../ss/logs/rolling";
//   @Value("${spider.log.directory}")
//   private String path;
   // 디비 접속 데이터
   @Value("${spring.datasource-data.url}")
   private String databaseUrl;
   @Value("${spring.datasource-data.username}")
   private String user;
   @Value("${spring.datasource-data.password}")
   private String password;

   public LogFileToDbBatch(JobRepository jobRepository, PlatformTransactionManager transactionManager,
         LogFileToDbService logFileToDbService) {
      super();
      this.jobRepository = jobRepository;
      this.transactionManager = transactionManager;
      this.logFileToDbService = logFileToDbService;
   }

   @Bean
   public Job logToDBJob() {
      return new JobBuilder("logToDBJob", jobRepository).start(logToDBStep()).build();
   }

   // tasklet으로 메서드 호출
   @Bean
   public Step logToDBStep() {

      return new StepBuilder("logToDBStep", jobRepository).tasklet(new Tasklet() {

         @Override
         public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

            // 롤링된 파일
//            File folder = new File(path+"/rolling");
            File folder = new File(path);
            File[] files = folder.listFiles();

            int count = 0;

            try (Connection connection = DriverManager.getConnection(databaseUrl, user, password);) {
               if (files != null) {
                  for (File file : files) {
//                	  if (file.isFile() && file.getName().startsWith("spider-")) {
                		  if (file.isFile()) {
                        count++;
                        System.out.println(file.getAbsolutePath());
                        logFileToDbService.executeLogFileToDb(connection, file.getAbsolutePath());
                     }
                     // 적재 후 파일 삭제, 실패 시 롤백 처리하고 강제 에러 발생
                     if (!file.delete()) {
                        connection.rollback();
                        throw new Exception("파일 삭제 실패");
                     }
                     
                  }
               }
            } catch (Exception e) {
               throw e;
            }

            return RepeatStatus.FINISHED;
         }
      }).transactionManager(transactionManager).build();
   }

}
