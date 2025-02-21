package neo.spider.admin.batch.dto;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@ToString
public class BatchJobInstanceDTO {

	private int instanceId;	
	private int version;
	private String jobName;
	private String jobKey;
	
	private BatchJobExecutionDTO exec;
	
	private List<BatchJobExecutionParamsDTO> execParams;

	// 검색을 위한 멤버변수
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date startDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date endDate;
	private String status;

	/*
	 * 동일 instanceId인 경우 구분하기 위한 executionId
	 *
	 * 하나의 job instance 에 여러 job execution 이 존재할 수 있음
	 * MyBatis ResultMap 은 <id>로 지정된 job instance 가 동일한 경우
	 * 새로운 객체를 생성하지 않고 기존 객체에 덮어씀
	 * 따라서 executionId 를 <id>로 추가하여 새로운 객체를 생성하도록
	 */
	private int executionId;
}
