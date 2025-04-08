package neo.spider.solution.E2E;

import java.io.IOException;

import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import neo.spider.solution.E2E.config.RestTemplateThresholdConfig;
import neo.spider.solution.E2E.constants.MDCKeys;
import neo.spider.solution.E2E.constants.RequestKeys;


@Component
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor{
//	private static final Logger logger = LoggerFactory.getLogger(RestTemplateInterceptor.class);
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {

		long threshold = RestTemplateThresholdConfig.getRestTemplateThresholdMs();
		
		String uri = request.getURI().toString();
		String called = MDC.get(MDCKeys.CURRENT);
		
		MDC.put(MDCKeys.CALLED_BY, MDC.get(MDCKeys.CURRENT));
		MDC.put(MDCKeys.CURRENT, uri);
		
		request.getHeaders().add(RequestKeys.TRACE_ID, MDC.get(MDCKeys.TRACE_ID));
		request.getHeaders().add(RequestKeys.USER_ID, MDC.get(MDCKeys.USER_ID));
		request.getHeaders().add(RequestKeys.USER_DEVICE, MDC.get(MDCKeys.USER_DEVICE));
		request.getHeaders().add(RequestKeys.USER_IP, MDC.get(MDCKeys.USER_IP));
		request.getHeaders().add(RequestKeys.CURRENT, uri);
		request.getHeaders().add(RequestKeys.CALLED_BY, called);

		// Before REST Call
		Long startTime = System.currentTimeMillis();
		
		MDC.put(MDCKeys.CALL_TIMESTAMP, startTime.toString());
		
//		logger.info("Before RestTemplate Call : called = {} ", called);
		LoggingUtils.log(null);
		

		try {
			ClientHttpResponse response = execution.execute(request, body);
			Long afterTime = System.currentTimeMillis();
			Long elapsedTime = afterTime - startTime;
			MDC.put(MDCKeys.CALL_TIMESTAMP, afterTime.toString());
			MDC.put(MDCKeys.EXECUTION_TIME, elapsedTime.toString());
			MDC.put(MDCKeys.RESPONSE_STATUS, response.getStatusCode().toString());
			if(elapsedTime > threshold) {
				MDC.put(MDCKeys.DELAY, "Network Delayed");
			}
			return response;
		} catch (Exception ex) {
			MDC.put(MDCKeys.ERROR, ex.getMessage());
			throw ex;
		} finally {
//			logger.info("After RestTemplate Call : called = {}", called);
			MDC.put(MDCKeys.CALLED_BY, MDC.get(MDCKeys.CURRENT));
			MDC.put(MDCKeys.CURRENT, called);
			
			
			
			LoggingUtils.log(null);
			
			MDC.remove(MDCKeys.DELAY);
			MDC.remove(MDCKeys.ERROR);
			MDC.remove(MDCKeys.EXECUTION_TIME);
		}

	}

	
}
