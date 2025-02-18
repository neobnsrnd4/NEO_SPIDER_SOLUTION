package neo.spider.solution.apimock.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ApiDTO {
	
	/* id(순번) */
	private int mockId;
	
	// wiremock_uuid
	private String mockWiremockId;
	
	/* API 이름 */
	private String mockApiName;
	
	/* API 주소 */
	private String mockApiUrl;
	
	/* 마지막 서버 동작 확인 시간 */
	private LocalDateTime mockLastCheckedTime;
	
	/* 마지막 서버 동작 상태(0:정상,1:장애,2:다운,3:지연,null:미확인) */
	private Integer mockLastCheckedStatus;
	
	/* 응답 상태(0:OFF/대응답,1:ON/실서버) */
	private Boolean mockResponseStatus; // HTTP 메서드 (GET, POST 등)
	
	private String mockApiRequestMethod;
    private String requestBody;     // 요청 바디
    private int responseStatusCode;     // 응답 상태 코드
    private String responseBody;    // 응답 본문

}
