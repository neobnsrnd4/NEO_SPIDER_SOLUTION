package neo.spider.solution.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AdminApiDTO {

	/* id(순번) */
	private int mockId;
	
	/* API 이름 */
	private String mockApiName;
	
	/* API 주소 */
	private String mockApiUrl;
	
	/* requestMethod  */
	private String mockApiRequestMethod;
	
	/* 마지막 서버 동작 확인 시간 */
	private LocalDateTime mockLastCheckedTime;
	
	/* 마지막 서버 동작 상태(0:정상,1:장애,2:다운,3:지연,null:미확인) */
	private Integer mockLastCheckedStatus;
	
	/* 응답 상태(0:OFF/대응답,1:ON/실서버) */
	private Boolean mockResponseStatus; // HTTP 메서드 (GET, POST 등)
}
