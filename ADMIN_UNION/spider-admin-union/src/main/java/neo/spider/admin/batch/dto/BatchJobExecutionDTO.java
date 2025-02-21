package neo.spider.admin.batch.dto;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BatchJobExecutionDTO {
	
	private int executionId;	
	private int version;	
	private int instanceId;		
	private Date createTime;
	private Date startTime;
	private Date endTime;
	private String status;
	private String exitCode;
	private String exitMessage;
	private Date updateTime;
	
    private boolean isStatusComplete;
    private boolean isExitComplete;
	
	private List<BatchStepExecutionDTO> steps;
	
}
