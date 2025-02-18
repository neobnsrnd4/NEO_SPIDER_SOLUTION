package neo.spider.solution.batch.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.MDC;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.transaction.Transactional;

@Service
public class BatchHistoryService {

	private final JdbcTemplate spiderTemplate;
	private final JobRegistry jobRegistry;

	public BatchHistoryService(JdbcTemplate spiderTemplate, JobRegistry jobRegistry) {
		super();
		this.spiderTemplate = spiderTemplate;
		this.jobRegistry = jobRegistry;
	}

	// 배치 실행 내역을 테이블 형식에 맞게 변환 해 전달
	public void saveBatchHistory(JobExecution jobExecution) {

		int maxLen = 3000;

		String batchAppId = MDC.get("batchAppId");
		String instanceId = MDC.get("instanceId");
		String batchDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String batchExecuteSeq = String.valueOf(jobExecution.getId());
		String logDtime = jobExecution.getStartTime() != null
				? jobExecution.getStartTime().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
				: "2025-01-01";
		String batchEndDtime = jobExecution.getEndTime() != null
				? jobExecution.getEndTime().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
				: logDtime;
		String resRtCode = jobExecution.getStatus().isUnsuccessful() ? "1" : "0";
		String lastUpdateUserId = MDC.get("userId") == null ? "Anonymous-user" : MDC.get("userId");
		String errorCode = jobExecution.getExitStatus().getExitCode();
		String errorReason = jobExecution.getExitStatus().getExitDescription().length() > maxLen
				? jobExecution.getExitStatus().getExitDescription().substring(0, maxLen)
				: jobExecution.getExitStatus().getExitDescription();
		Integer recordCount = (int) jobExecution.getStepExecutions().stream().mapToLong(StepExecution::getWriteCount)
				.sum();
		Integer failCount = (int) jobExecution.getStepExecutions().stream().mapToLong(StepExecution::getWriteSkipCount)
				.sum();
		Integer executeCount = recordCount + failCount;
		Integer successCount = recordCount;

		String jobSql = "INSERT INTO R_SPIDERLINK.FWK_BATCH_HIS ("
				+ "BATCH_APP_ID, INSTANCE_ID, BATCH_DATE, BATCH_EXECUTE_SEQ, LOG_DTIME, "
				+ "BATCH_END_DTIME, RES_RT_CODE, LAST_UPDATE_USER_ID, ERROR_CODE, ERROR_REASON, "
				+ "RECORD_COUNT, EXECUTE_COUNT, SUCCESS_COUNT, FAIL_COUNT) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		System.out.println(batchAppId + " " + instanceId + " " + batchDate + " " + batchExecuteSeq + " "
				+ logDtime + " " + batchEndDtime + " " + resRtCode + " " + lastUpdateUserId + " " + errorCode + " "
				+ errorReason + " " + recordCount + " " + executeCount + " " + successCount + " " + failCount);

		spiderTemplate.update(jobSql, batchAppId, instanceId, batchDate, batchExecuteSeq, logDtime, batchEndDtime,
				resRtCode, lastUpdateUserId, errorCode, errorReason, recordCount, executeCount, successCount,
				failCount);

	}

}
