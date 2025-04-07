package neo.spider.solution.E2E;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import neo.spider.solution.E2E.constants.MDCKeys;


public class LoggingUtils {

	private static final Logger traceLogger = LoggerFactory.getLogger("neo.spider.solution.E2E");
//	private static final Logger traceLogger = LoggerConfig.getLogger(LoggingUtils.class);
	private static final Logger errorLogger = LoggerFactory.getLogger("ERROR");
	
	public static void log(String msg) {
		String traceId = MDC.get(MDCKeys.TRACE_ID);
		String requestUri = MDC.get(MDCKeys.REQUEST_URL);
		String userId = MDC.get(MDCKeys.USER_ID);
		String userIp = MDC.get(MDCKeys.USER_IP);
		String userDevice = MDC.get(MDCKeys.USER_DEVICE);
		String calledBy = MDC.get(MDCKeys.CALLED_BY);
		String current = MDC.get(MDCKeys.CURRENT);
		String callTimestamp = MDC.get(MDCKeys.CALL_TIMESTAMP);
		String executionTime = MDC.get(MDCKeys.EXECUTION_TIME);
		String responseStatus = MDC.get(MDCKeys.RESPONSE_STATUS);
		String error = MDC.get(MDCKeys.ERROR);
		String delay = MDC.get(MDCKeys.DELAY);
		String query = MDC.get(MDCKeys.QUERY);
		
		traceLogger.info("[{}], [{}], [{}], [{}], [{}], [{}], [{}], [{}], [{}], [{}], [{}], [{}], [{}]"
				,traceId, requestUri, userId, userIp, userDevice, calledBy, current, callTimestamp, executionTime,
				responseStatus, error, delay, query
				);
		
	}
	
	public static void logException(Exception ex) {
		errorLogger.error("Exception By Logging JAR : {}", ex.getMessage());
	}
}
