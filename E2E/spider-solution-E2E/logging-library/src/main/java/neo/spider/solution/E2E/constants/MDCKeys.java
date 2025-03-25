package neo.spider.solution.E2E.constants;

public final class MDCKeys {
	
	private MDCKeys() {}	//인스턴스로 생성하지 못하도록 기본 생성자 private

	//한 요청임을 나타내는 
	public static final String TRACE_ID = "traceId";
	//요청 URI
	public static final String REQUEST_URL = "requestURL";
	//사용자 ID
	public static final String USER_ID = "userId";
	//사용자 IP
	public static final String USER_IP = "userIP";
	//사용자 장치 정보
	public static final String USER_DEVICE = "device";
	//이전 호출에 대한 정보(사용자,메서드)
	public static final String CALLED_BY = "calledBy";
	//요청 흐름에서 MDC의 현재 위치
	public static final String CURRENT = "current";
	//메서드가 호출된 시간
	public static final String CALL_TIMESTAMP = "callTimestamp";
	//메서드가 실행되는데 걸린 시간(ms)
	public static final String EXECUTION_TIME = "executionTime";
	//응답 상태 코드(2xx,4xx,5xx)
	public static final String RESPONSE_STATUS = "responseStatus";
	//에러 사유
	public static final String ERROR = "error";
	//지연 사유
	public static final String DELAY = "delay";
	//실행한 쿼리 내용
	public static final String QUERY = "query";
	
	
	
}
