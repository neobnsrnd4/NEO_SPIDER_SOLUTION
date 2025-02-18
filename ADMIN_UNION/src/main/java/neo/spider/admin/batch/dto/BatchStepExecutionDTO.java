package neo.spider.admin.batch.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BatchStepExecutionDTO {
	
	private int stepId;	
	private int version;
	private String stepName;
	private int executionId;
	private Date createTime;
	private Date startTime;
	private Date endTime;
	private String status;
	private int commitCount;
	private int readCount;
	private int filterCount;
	private int writeCount;
	private int readSkipCount;
	private int writeSkipCount;
	private int processSkipCount;
	private int rollbackCount;
	private String exitCode;
	private String exitMessage;
	private Date updateTime;
	private String configurationLocation;
	
    private boolean isStatusComplete;
    private boolean isExitComplete;
	
    public void setStatus(String status) {
        this.status = status;
        setStatusComplete("COMPLETED".equals(status));  
    }   
    public void setExitCode(String exitCode) {
        this.exitCode = exitCode;
        setExitComplete("COMPLETED".equals(exitCode));  
    }      
	
}
