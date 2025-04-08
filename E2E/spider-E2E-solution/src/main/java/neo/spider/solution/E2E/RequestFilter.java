package neo.spider.solution.E2E;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import neo.spider.solution.E2E.config.EnableLoggingConfig;
import neo.spider.solution.E2E.constants.MDCKeys;
import neo.spider.solution.E2E.constants.RequestKeys;

public class RequestFilter extends OncePerRequestFilter{
	
//	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		Long call_timestamp = System.currentTimeMillis();
		//헤더에서 값 꺼내기(trace_id, 요청IP, 요청URL, 사용자(id,device), calledBy
		String trace_id = request.getHeader(RequestKeys.TRACE_ID);
		String request_url = request.getRequestURL().toString() + "/" + request.getMethod();
		String user_id = request.getHeader(RequestKeys.USER_ID);
		String user_device = request.getHeader(RequestKeys.USER_DEVICE);
		String user_ip = request.getRemoteAddr();
		String called_by = request.getHeader(RequestKeys.CALLED_BY);
		String current = request.getHeader(RequestKeys.CURRENT);
		
		boolean isNew = (trace_id==null || trace_id.isEmpty());
		if(isNew) {
			trace_id = UUID.randomUUID().toString();
			request_url = request.getRequestURL().toString() + "/" + request.getMethod();
			user_id = "user-" + UUID.randomUUID().toString().substring(0, 8);
			user_device = exchangeDeviceInfo(request.getHeader("User-Agent"));
			called_by = "USER";
			current = request_url;
		}
		
		MDC.put(MDCKeys.TRACE_ID, trace_id);
		MDC.put(MDCKeys.REQUEST_URL, request_url);
		MDC.put(MDCKeys.USER_ID, user_id);
		MDC.put(MDCKeys.USER_DEVICE, user_device);
		MDC.put(MDCKeys.USER_IP, user_ip);
		MDC.put(MDCKeys.CALLED_BY, called_by);
		MDC.put(MDCKeys.CURRENT, current);
		MDC.put(MDCKeys.CALL_TIMESTAMP, call_timestamp.toString());
		
        if(isNew && EnableLoggingConfig.getFilterLogging()) {
//        	logger.info("라이브러리의 필터 동작");
        	LoggingUtils.log(null);
        	
        }
        try {
            filterChain.doFilter(request, response);
            
            //응답 헤더에 값 추가
            
            
        }catch(Exception e){
        	MDC.put(MDCKeys.ERROR, e.getMessage());
        	MDC.put(MDCKeys.RESPONSE_STATUS, String.valueOf(response.getStatus()));
        }
        finally {
        	MDC.put(MDCKeys.CALLED_BY, MDC.get(MDCKeys.CURRENT));
			MDC.put(MDCKeys.CURRENT, called_by);
        	Long afterRequest = System.currentTimeMillis();
        	Long elapsedTime = afterRequest - call_timestamp;
        	
        	MDC.put(MDCKeys.CALL_TIMESTAMP, afterRequest.toString());
        	MDC.put(MDCKeys.EXECUTION_TIME, elapsedTime.toString());
//        	MDC.put(MDCKeys.RESPONSE_STATUS, String.valueOf(responseWrapper.getStatus()));
        	
        	if(isNew && EnableLoggingConfig.getFilterLogging()) {
        		LoggingUtils.log(null);
        	}
        	
        	response.addHeader(RequestKeys.TRACE_ID, trace_id);
        	response.addHeader(RequestKeys.USER_ID, user_id);
        	response.addHeader(RequestKeys.USER_DEVICE, user_device);
        	response.addHeader(RequestKeys.CALLED_BY, called_by);
        	response.addHeader(RequestKeys.CURRENT, current);
        	
            // MDC 정리
            MDC.clear();
        }
    }

	
	public String exchangeDeviceInfo(String user_device) {
		if(user_device == null)
			return "UNKNOW";
		else if(user_device.contains("Mobile"))
			return "Mobile";
		else
			return "Desktop";
	}
}
