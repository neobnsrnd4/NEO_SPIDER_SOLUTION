package neo.spider.solution.batch.batchJobs;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.cloud.function.context.config.ContextFunctionCatalogInitializer.DummyProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class DbToDbBatch {

	private final DataSource realSource;
	private final DataSource targetSource;
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final CustomBatchJobListener listener;

	public DbToDbBatch(@Qualifier("commonDataSource") DataSource realSource,
			@Qualifier("targetDataSource") DataSource targetSource, JobRepository jobRepository,
			PlatformTransactionManager transactionManager, CustomBatchJobListener listener) {
		this.realSource = realSource;
		this.targetSource = targetSource;
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.listener = listener;
	}

	@Bean
	public JdbcPagingItemReader<Map<String, Object>> reader() throws Exception {
		JdbcPagingItemReader<Map<String, Object>> reader = new JdbcPagingItemReader<Map<String, Object>>();
		reader.setDataSource(realSource);
		reader.setName("pagingReader");
		reader.setQueryProvider(queryProvider());
		reader.setRowMapper((rs, rowNum) -> {
			Map<String, Object> map = new HashMap<>();
			map.put("accountNumber", rs.getString("ACCOUNT_NUMBER"));
			map.put("money", rs.getLong("ACCOUNT_BALANCE"));
			map.put("name", rs.getString("CUSTOMER_NAME"));
			return map;
		});
		reader.setPageSize(10);
		return reader;
	}

	@Bean
	public PagingQueryProvider queryProvider() throws Exception {
		SqlPagingQueryProviderFactoryBean factory = new SqlPagingQueryProviderFactoryBean();
		factory.setDataSource(realSource);
		factory.setSelectClause("SELECT *");
		factory.setFromClause("FROM FWK_BATCH_CUSTOMER_ACCOUNT");
		factory.setSortKey("ACCOUNT_ID");
		return factory.getObject();
	}

	@Bean
	public JdbcBatchItemWriter<Map<String, Object>> writer() {
		return new JdbcBatchItemWriterBuilder<Map<String, Object>>().dataSource(targetSource)
				.sql("INSERT INTO FWK_BATCH_CUSTOMER_ACCOUNT(ACCOUNT_NUMBER, ACCOUNT_BALANCE, CUSTOMER_NAME) VALUES (:accountNumber, :money, :name)")
				.itemSqlParameterSourceProvider(item -> {
					MapSqlParameterSource params = new MapSqlParameterSource();
					params.addValue("accountNumber", item.get("accountNumber"));
					params.addValue("money", item.get("money"));
					params.addValue("name", item.get("name"));
					return params;
				}).build();
	}

	@Bean
	public Step step() throws Exception {

		int chunkSize = 500; // 10, 50, 100

		return new StepBuilder("dbCopyStep", jobRepository)
				.<Map<String, Object>, Map<String, Object>>chunk(chunkSize, transactionManager).reader(reader())
				.processor(dummyProcessor1()).writer(writer()).taskExecutor(taskExecutor()).build();
	}

	@Bean
	public ItemProcessor<Map<String, Object>, Map<String, Object>> dummyProcessor1() {
		return new ItemProcessor<Map<String, Object>, Map<String, Object>>() {

			@Override
			public Map<String, Object> process(Map<String, Object> item) throws Exception {
				String threadName = Thread.currentThread().getName();
				return item;
			}
		};
	}

	@Bean
	public Job job() throws Exception {
		return new JobBuilder("dbCopyJob", jobRepository).listener(listener).start(step()).build();
	}

	@Bean
	public TaskExecutor taskExecutor() {

		int corePoolSize = 4; // 4~8
		int maxPoolSize = 8; // 8~16
		int queueSize = 50; // 50~100

		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setQueueCapacity(queueSize);
		executor.setThreadNamePrefix("dbCopyTask");
		executor.initialize();
		return executor;
	}

}
