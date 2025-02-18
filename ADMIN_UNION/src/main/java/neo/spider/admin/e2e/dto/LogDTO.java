package neo.spider.admin.e2e.dto;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Alias("LogDTO")
@Getter
@Setter
@ToString
public class LogDTO {

	private Long timestamp; // 메서드 호출 시간, 요청이 서버에 입장한 시간...
	private String traceId;
	private String requestUrl;
	private String userId;
	private String userIp;
	private String userDeviceCd;
	private String callerComponentName;
	private String targetComponentName;
	private Long executionTime;
	private String responseStatusCd;
	private String errorMessageText;
	private String delayMessageText;
	private String query;

	// 검색 파라미터 저장용
	private String executeResult;
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime startTime;
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime endTime;

	//mybatis 검색 파라미터용
	private Long ltTimestamp;
	private Long gtTimestamp;
	private Long searchExecuteTime;
}
