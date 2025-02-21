package neo.spider.admin.batch.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BatchJobExecutionParamsDTO {
	
	private int executionId;
	private String parameterName;
	private String parameterType;
	private String parameterValue;
	private String identifying;

}
