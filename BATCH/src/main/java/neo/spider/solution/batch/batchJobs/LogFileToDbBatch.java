package neo.spider.solution.batch.batchJobs;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.validation.BindException;

import neo.spider.solution.batch.dto.LogDTO;

@Configuration
public class LogFileToDbBatch {
	private final DataSource datasource;
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final CustomBatchJobListener listener;
	private String path = "\\spider\\logs\\rolling\\spider-*.log";
	private int chunkSize = 500;

	public LogFileToDbBatch(@Qualifier("commonDataSource") DataSource datasource, JobRepository jobRepository,
			PlatformTransactionManager transactionManager, CustomBatchJobListener listener) {
		super();
		this.datasource = datasource;
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.listener = listener;
	}

	@Bean
	public Job logToDBJob() {
		return new JobBuilder("logToDBJob", jobRepository).listener(listener).start(logToDBStep()).build();
	}

	@Bean
	public Step logToDBStep() {

		return new StepBuilder("logToDBStep", jobRepository).<LogDTO, LogDTO>chunk(chunkSize, transactionManager)
				.reader(multiResourceItemReader()).writer(conditionalWriters()).taskExecutor(syncTaskExecutor())
				.build();
	}

	@Bean
	public ItemProcessor<LogDTO, LogDTO> dummyProcessor3() {
		return new ItemProcessor<LogDTO, LogDTO>() {

			@Override
			public LogDTO process(LogDTO item) throws Exception {
				String threadName = Thread.currentThread().getName();
				return item;
			}
		};
	}

	@Bean
	public TaskExecutor syncTaskExecutor() {
		// 단일 스레드로 실행
		return new SyncTaskExecutor();
	}

	@Bean
	public TaskExecutor logToDBTaskExecutor() {

		int corePoolSize = 4; // 4~8
		int maxPoolSize = 8; // 8~16
		int queueSize = 50; // 50~100

		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setQueueCapacity(queueSize);
		executor.setThreadNamePrefix("logToDBTask");
		executor.initialize();

		return executor;
	}

	// 멀티 리소스 리더를 통해 롤링된 파일을 멀티 리소스로 지정
	@Bean
	public MultiResourceItemReader<LogDTO> multiResourceItemReader() {

		MultiResourceItemReader<LogDTO> multiReader = new MultiResourceItemReader<>();
		ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();

		// 롤링된 분단위 리소스 지정
		Resource[] resource;
		try {
			// 프로젝트 폴더 경로 구한 후 \를 /로 바꿈
			String rootPath = System.getProperty("user.dir");
			int lastIndex = rootPath.lastIndexOf("\\");
			String rolledPath = "file:" + rootPath.substring(0, lastIndex) + path;
			rolledPath = rolledPath.replace("\\", "/");
			resource = patternResolver.getResources(rolledPath);
			System.out.println("resource len : " + resource.length);
			multiReader.setResources(resource);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 개별 파일을 담당할 리더 지정
		multiReader.setDelegate(logReader());

		return multiReader;

	}

	@Bean
	public FlatFileItemReader<LogDTO> logReader() {

		FlatFileItemReader<LogDTO> reader = new FlatFileItemReader<>();

		DefaultLineMapper<LogDTO> lineMapper = new DefaultLineMapper<>() {
			// 파일의 라인 넘버를 로그에 저장해 plantUML 이 순서대로 그려지게 함
			@Override
			public LogDTO mapLine(String line, int lineNumber) throws Exception {
				LogDTO log = super.mapLine(line, lineNumber);
				log.setFileSequence(Long.valueOf(lineNumber));
				return log;
			}
		};

		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		tokenizer.setDelimiter(";");
		tokenizer.setNames("timestmp", "solutionName", "timestamp", "traceId", "requestUrl", "userId", "userIp",
				"userDeviceCd", "callerComponentName", "targetComponentName", "executionTime", "responseStatusCd",
				"errorMessageText", "delayMessageText", "query");
		lineMapper.setLineTokenizer(tokenizer);

		BeanWrapperFieldSetMapper<LogDTO> fieldSetMapper = new BeanWrapperFieldSetMapper<>() {

			@Override
			public LogDTO mapFieldSet(FieldSet fs) throws BindException {

				LogDTO log = new LogDTO();

				log.setTimestmp(fs.readString("timestmp"));
				log.setSolutionName(fs.readString("solutionName"));
				log.setTimestamp(parseLongSafe(fs.readString("timestamp")));
				log.setTraceId(fs.readString("traceId"));
				log.setResponseStatusCd(fs.readString("requestUrl"));
				log.setUserId(fs.readString("userId"));
				log.setUserIp(fs.readString("userIp"));
				log.setUserDeviceCd(fs.readString("userDeviceCd"));
				log.setCallerComponentName(fs.readString("callerComponentName"));
				log.setTargetComponentName(fs.readRawString("targetComponentName"));
				log.setExecutionTime(parseLongSafe(fs.readString("executionTime")));
				log.setResponseStatusCd(fs.readString("responseStatusCd"));
				log.setErrorMessageText(fs.readString("errorMessageText"));
				log.setDelayMessageText(fs.readString("delayMessageText"));
				log.setQuery(fs.readString("query"));

				return log;
			}

			// timestamp, executionTime이 빈 값인 경우 데이터 변환 예외를 막고 null 반환
			private Long parseLongSafe(String value) {
				value = value.trim();

				try {
					return (value == null || value.isEmpty()) ? null : Long.parseLong(value);
				} catch (NumberFormatException e) {
					return null;
				}
			}
		};

		lineMapper.setFieldSetMapper(fieldSetMapper);
		reader.setLineMapper(lineMapper);

		return reader;
	}

	@Bean
	public ItemWriter<LogDTO> loggingEventWriter() {

		String sql = "INSERT INTO FWK_E2E_LOGGING_EVENT_TEST (TIMESTAMP, TRACE_ID, REQUEST_URL, USER_ID, USER_IP, USER_DEVICE_CD, CALLER_COMPONENT_NAME, TARGET_COMPONENT_NAME, EXECUTION_TIME, RESPONSE_STATUS_CD, ERROR_MESSAGE_TEXT, DELAY_MESSAGE_TEXT, QUERY, FILE_SEQUENCE) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		return new JdbcBatchItemWriterBuilder<LogDTO>().dataSource(datasource).sql(sql)
				.itemPreparedStatementSetter(new eventWriterItemPSSetter()).build();

	}

	public class eventWriterItemPSSetter implements ItemPreparedStatementSetter<LogDTO> {

		@Override
		public void setValues(LogDTO item, PreparedStatement ps) throws SQLException {
			if (item.getTimestamp() != null) {
				ps.setLong(1, item.getTimestamp());
			} else {
				ps.setNull(1, Types.BIGINT);
			}

			ps.setString(2, item.getTraceId());
			ps.setString(3, item.getRequestUrl());
			ps.setString(4, item.getUserId());
			ps.setString(5, item.getUserIp());
			ps.setString(6, item.getUserDeviceCd());
			ps.setString(7, item.getCallerComponentName());
			ps.setString(8, item.getTargetComponentName());

			if (item.getExecutionTime() != null) {
				ps.setLong(9, item.getExecutionTime());
			} else {
				ps.setNull(9, Types.BIGINT);
			}

			ps.setString(10, item.getResponseStatusCd());
			ps.setString(11, item.getErrorMessageText());
			ps.setString(12, item.getDelayMessageText());
			ps.setString(13, item.getQuery());
			ps.setLong(14, item.getFileSequence());

		}

	}

	@Bean
	public ItemWriter<LogDTO> loggingSlowWriter() {

		String sql = "INSERT INTO FWK_E2E_LOGGING_DELAY_TEST (TIMESTAMP, TRACE_ID, REQUEST_URL, USER_ID, USER_IP, USER_DEVICE_CD, CALLER_COMPONENT_NAME, TARGET_COMPONENT_NAME, EXECUTION_TIME, RESPONSE_STATUS_CD, ERROR_MESSAGE_TEXT, DELAY_MESSAGE_TEXT, QUERY) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		return new JdbcBatchItemWriterBuilder<LogDTO>().dataSource(datasource).sql(sql)
				.itemPreparedStatementSetter(new slowWriterItemPSSetter()).build();

	}

	public class slowWriterItemPSSetter implements ItemPreparedStatementSetter<LogDTO> {

		@Override
		public void setValues(LogDTO item, PreparedStatement ps) throws SQLException {

			if (item.getTimestamp() != null) {
				ps.setLong(1, item.getTimestamp());
			} else {
				ps.setNull(1, Types.BIGINT);
			}

			ps.setString(2, item.getTraceId());
			ps.setString(3, item.getRequestUrl());
			ps.setString(4, item.getUserId());
			ps.setString(5, item.getUserIp());
			ps.setString(6, item.getUserDeviceCd());
			ps.setString(7, item.getCallerComponentName());
			ps.setString(8, item.getTargetComponentName());

			if (item.getExecutionTime() != null) {
				ps.setLong(9, item.getExecutionTime());
			} else {
				ps.setNull(9, Types.BIGINT);
			}

			ps.setString(10, item.getResponseStatusCd());
			ps.setString(11, item.getErrorMessageText());
			ps.setString(12, item.getDelayMessageText());
			ps.setString(13, item.getQuery());

		}

	}

	@Bean
	public ItemWriter<LogDTO> loggingErrorWriter() {

		// 성능 개선을 위해 ps 방식으로 변경
		String sql = "INSERT INTO FWK_E2E_LOGGING_ERROR_TEST (TIMESTAMP, TRACE_ID, REQUEST_URL, USER_ID, USER_IP, USER_DEVICE_CD, CALLER_COMPONENT_NAME, TARGET_COMPONENT_NAME, EXECUTION_TIME, RESPONSE_STATUS_CD, ERROR_MESSAGE_TEXT, DELAY_MESSAGE_TEXT, QUERY) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		return new JdbcBatchItemWriterBuilder<LogDTO>().dataSource(datasource).sql(sql)
				.itemPreparedStatementSetter(new errorWriterItemPSSetter()).build();

	}

	public class errorWriterItemPSSetter implements ItemPreparedStatementSetter<LogDTO> {

		@Override
		public void setValues(LogDTO item, PreparedStatement ps) throws SQLException {
			if (item.getTimestamp() != null) {
				ps.setLong(1, item.getTimestamp());
			} else {
				ps.setNull(1, Types.BIGINT);
			}

			ps.setString(2, item.getTraceId());
			ps.setString(3, item.getRequestUrl());
			ps.setString(4, item.getUserId());
			ps.setString(5, item.getUserIp());
			ps.setString(6, item.getUserDeviceCd());
			ps.setString(7, item.getCallerComponentName());
			ps.setString(8, item.getTargetComponentName());

			if (item.getExecutionTime() != null) {
				ps.setLong(9, item.getExecutionTime());
			} else {
				ps.setNull(9, Types.BIGINT);
			}

			ps.setString(10, item.getResponseStatusCd());
			ps.setString(11, item.getErrorMessageText());
			ps.setString(12, item.getDelayMessageText());
			ps.setString(13, item.getQuery());

		}

	}

	@Bean
	public ItemWriter<LogDTO> conditionalWriters() {

		return items -> {

			List<LogDTO> eventList = new ArrayList<>();
			List<LogDTO> delayList = new ArrayList<>();
			List<LogDTO> errorList = new ArrayList<>();

			for (LogDTO log : items) {

				// 이벤트 추가
				eventList.add(log);

				if (!log.getDelayMessageText().trim().isEmpty()) {
					delayList.add(log);
				} else if (!log.getErrorMessageText().trim().isEmpty()) {
					errorList.add(log);
				}
			}

			Chunk<LogDTO> chunk = new Chunk<LogDTO>(eventList);

			loggingEventWriter().write(new Chunk<LogDTO>(eventList));
			loggingSlowWriter().write(new Chunk<LogDTO>(delayList));
			loggingErrorWriter().write(new Chunk<LogDTO>(errorList));

			eventList.clear();
			delayList.clear();
			errorList.clear();

		};
	}

}
