package neo.spider.solution.batch.dto;

import lombok.Data;

@Data
public class LogDTO {

	private String timestmp;
	private String solutionName;
	private Long timestamp;
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
	private Long fileSequence;

}
