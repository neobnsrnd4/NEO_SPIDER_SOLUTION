package neo.spider.solution.E2E.constants;

public final class RequestKeys {
	
	private RequestKeys() {}
	
	// 요청 추적을 위한 고유 ID
    public static final String TRACE_ID = "X-Trace-Id";

    // 요청 URI
    public static final String REQUEST_URL = "X-Request-Url";

    // 사용자 ID
    public static final String USER_ID = "X-User-Id";
    //사용자 IP
    public static final String USER_IP = "X-User-IP";

    // 사용자의 장치 정보 (예: phone, pc 등)
    public static final String USER_DEVICE = "X-Device";

    // 이전 호출 정보 (클래스 및 메서드)
    public static final String CALLED_BY = "X-Called-By";

    // 현재 메서드 정보
    public static final String CURRENT = "X-Current";

    // 메서드 호출 시간
    public static final String CALL_TIMESTAMP = "X-Call-Timestamp";

    // 메서드 실행 시간 (밀리초 단위)
    public static final String EXECUTION_TIME = "X-Execution-Time";

    // 응답 상태 코드
    public static final String RESPONSE_STATUS = "X-Response-Status";

    // 에러 사유
    public static final String ERROR = "X-Error";

    // 지연 사유
    public static final String DELAY = "X-Delay";

    // 실행된 쿼리 내용
    public static final String QUERY = "X-Query";
}
