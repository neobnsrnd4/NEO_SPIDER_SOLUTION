package neo.spider.solution.apimock.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import neo.spider.solution.apimock.dto.ApiDTO;
import neo.spider.solution.apimock.mapper.ApiMapper;

@Service
@RequiredArgsConstructor
public class ApiServiceImpl implements ApiService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final ApiMapper apiMapper;
	private final RestTemplate restTemplate;
	private final String WIREMOCK_ADMIN_URL = "http://localhost:56789/__admin/mappings"; 


	@Override
	public List<ApiDTO> getAllApis() {
		return apiMapper.findAll();
	}
	
	@Override
	public ApiDTO getApi(int id) {
		return apiMapper.findById(id);
	}
	
	@Override
	public List<ApiDTO> getApis(List<Integer> ids) {
		return apiMapper.findByIds(ids);
	}

	@Override
	public void saveNewApi(ApiDTO apiDto) { // 프론트에서 url 넘겨주면 uri로 잘라야함
		String fullUrl = apiDto.getMockApiUrl();
		String uri = extractUri(fullUrl);
		if(uri == null || uri.equals("")) {
			uri = "/";
		}
		// WireMock에 등록할 JSON 데이터 생성
        Map<String, Object> requestBody = new HashMap<>();

        // 기본 request 설정
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("method", apiDto.getMockApiRequestMethod());
        requestMap.put("urlPathPattern", uri);
        requestMap.put("queryParameters", parseQueryParams(extractQuery(apiDto.getMockApiUrl())));

        // `requestBody`가 존재하면 `bodyPatterns` 추가
        if (apiDto.getRequestBody() != null && !apiDto.getRequestBody().isEmpty()) {
            requestMap.put("bodyPatterns", List.of(
                Map.of("equalToJson", apiDto.getRequestBody()) // JSON 비교
            ));
        }

        requestBody.put("request", requestMap);
        requestBody.put("response", Map.of(
            "status", apiDto.getResponseStatusCode(),
            "body", apiDto.getResponseBody(),
            "transformers", List.of("response-template")
        ));
        
        // WireMock에 API 등록 요청 보내기
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<Map> response = restTemplate.exchange(
        		WIREMOCK_ADMIN_URL,
                HttpMethod.POST,
                requestEntity,
                Map.class
            );
        
        String wiremockId = response.getBody().get("uuid").toString();
        apiDto.setMockWiremockId(wiremockId);
        
        // stub save
        HttpEntity<String> saveRequest = new HttpEntity<>("{}", headers);
        restTemplate.postForEntity(WIREMOCK_ADMIN_URL + "/save", saveRequest, String.class);
        
        apiMapper.saveApi(apiDto);
	}
	
	// 정규식 검증
	private boolean containsRegex(String url) {
	    return url.contains("[0-9]+") || 
	           url.contains("[a-zA-Z]+") || 
	           url.contains("[a-zA-Z0-9]+") || 
	           url.contains("[a-zA-Z0-9-]+") || 
	           url.contains(".*");
	}
	
	// Query String을 WireMock에서 처리할 수 있도록 변환
    private Map<String, Map<String, String>> parseQueryParams(String queryParams) {
        Map<String, Map<String, String>> parsedParams = new HashMap<>();
        if (queryParams != null && !queryParams.isEmpty()) {
            for (String param : queryParams.split("&")) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    parsedParams.put(keyValue[0], Map.of("equalTo", keyValue[1]));
                }
            }
        }
        return parsedParams;
    }
    // uri 제외 url
    private String extractDomain(String url) {
        try {
        	boolean hasRegex = containsRegex(url);
        	String processedUrl = hasRegex ? url.replaceAll("\\[.*?\\]", "__TEMP_PLACEHOLDER__") : url;
            URI uri = new URI(processedUrl);
            return uri.getScheme() + "://" + uri.getHost(); // 도메인만 반환
        } catch (Exception e) {
            throw new RuntimeException("잘못된 URL 형식: " + url);
        }
    }
    
    // URL에서 URI 추출
    private String extractUri(String url) {
        try {
        	boolean hasRegex = containsRegex(url);
        	String processedUrl = hasRegex ? url.replaceAll("\\[.*?\\]", "__TEMP_PLACEHOLDER__") : url;
        	URI uri = new URI(processedUrl);
        	String path = uri.getPath(); // 도메인 제외한 경로 부분만 반환 (예: "/users")
        
        	// 정규식이 포함된 경우, 원래 정규식을 복원해서 반환
        	return hasRegex ? path.replace("__TEMP_PLACEHOLDER__", url.substring(url.indexOf("["))) : path;
        } catch (URISyntaxException e) {
            throw new RuntimeException("잘못된 URL 형식: " + url);
        }
    }
    
    // URL에서 Query Param 추출
    private String extractQuery(String url) {
    	 try {
    		 boolean hasRegex = containsRegex(url);
         	 String processedUrl = hasRegex ? url.replaceAll("\\[.*?\\]", "__TEMP_PLACEHOLDER__") : url;
    	     URI uri = new URI(processedUrl);
             return uri.getQuery();
         } catch (URISyntaxException e) {
             throw new RuntimeException("잘못된 URL 형식: " + url);
         }
    }

	@Override
	public void updateCheckedApiInfo(int id, LocalDateTime checkedTime, Integer checkedStatus) {
		ApiDTO apiDTO = new ApiDTO();
		apiDTO.setMockId(id);
		apiDTO.setMockLastCheckedTime(checkedTime);
		apiDTO.setMockLastCheckedStatus(checkedStatus);
		apiMapper.updateCheckedStatus(apiDTO);
	}

	@Override
	public void toggleResponseStatusById(int id) {
		apiMapper.toggleResponseStatusById(id);
	}

	@Override
	public void changeModeById(int id, boolean targetMode) {
		ApiDTO apiDTO = getApi(id);
		apiDTO.setMockResponseStatus(targetMode);
		apiMapper.changeResponseStatusById(apiDTO);
	}
	
	@Override
	public void changeModeByIds(List<Integer> ids, boolean targetMode) {
		List<Integer> idsToUpdate = getApis(ids).stream()
				.filter(api -> api.getMockResponseStatus() != targetMode)
				.map(ApiDTO::getMockId)
				.collect(Collectors.toList());
		if(idsToUpdate.isEmpty()) return;
		try {
			apiMapper.toggleResponseStatusByIds(idsToUpdate);
	    } catch (Exception e) {
	        logger.error("Unexpected error 발생: ", e);
	        return;
	    }
	}

	@Override
	public ApiDTO performHealthCheck(int id) {
		ApiDTO apiDTO = apiMapper.findById(id);
		if(apiDTO == null) throw new IllegalArgumentException("해당 ID의 API가 존재하지 않습니다.");
		
		String apiUrl = apiDTO.getMockApiUrl();
		int statusCode = 0;	//0: 정상, 1-3: 비정상 
		
		try {
			ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
			//장애 판별 로직: Content-Type이 json인지 검사하여 판별(Content-type이 없거나 html이라도 json 결과값이 파싱되면 정상 처리)
			if(response.getStatusCode() == HttpStatus.OK) {
				if(response.getHeaders().getContentType() != null 
						&& response.getHeaders().getContentType().toString().contains("application/json")) {
					statusCode = 0;
				} else {
					ObjectMapper objectMapper = new ObjectMapper();
					try {
						objectMapper.readTree(response.getBody());
						statusCode = 0;
					} catch(Exception e) {
						statusCode = 1;
					}
				}
			} else {
				statusCode = 1;
			}
		} catch(Exception e) {
			statusCode = handleException(apiUrl, e);
		}
		
		updateCheckedApiInfo(id, LocalDateTime.now(), statusCode);
		if(statusCode != 0) changeModeById(id, false);//서버 문제 시, 자동 대응답 처리
		return apiMapper.findById(id);
	}
	
	@Override
	public void checkAllApiHealthCheck() {
		List<ApiDTO> apis = getAllApis();
		
		//고정된 스레드 풀 생성(최대 10개 스레드)
	    ExecutorService executorService = Executors.newFixedThreadPool(10);
        apis.forEach(api -> executorService.submit(() -> {
        	try {
        		performHealthCheck(api.getMockId());
        	} catch(Exception e) {
        		logger.error("HealthCheck 실패: ID = " + api.getMockId(), e);
        	}
        }));
        executorService.shutdown();
	}
	
	//=== FUNCTION ===//
	private int handleException(String apiUrl, Exception e) {
		if(e.getCause() instanceof java.net.SocketTimeoutException) {
			logger.error("|!!! HEALTHCHECK INFO : FAILED !!!| ※ API Timeout 발생: " + apiUrl);
			return 3;
		} else if(e.getCause() instanceof java.net.ConnectException
				|| e.getCause() instanceof java.net.UnknownHostException) {
			logger.error("|!!! HEALTHCHECK INFO : FAILED !!!| ※ API 서버 다운 발생: " + apiUrl);
			return 2;
		} else {
			logger.error("|!!! HEALTHCHECK INFO : FAILED !!!| ※ API 서버 장애 발생: " + apiUrl);
			//logger.error("|!!! HEALTHCHECK INFO : FAILED !!!| ※ API 서버 장애 발생: " + apiUrl, e);//==> 상세 정보 확인 필요 시 전환
			return 1;
		}
	}

	@Override
	public void deleteApi(int id) {
		ApiDTO apiDTO = apiMapper.findById(id);
		
		String wiremockId = apiDTO.getMockWiremockId();
		
		if (wiremockId == null) {
            throw new RuntimeException("해당 ID에 대한 Mock API를 찾을 수 없습니다.");
        }
		
		String wiremockUrl = WIREMOCK_ADMIN_URL +"/"+ wiremockId;
        ResponseEntity<String> response = restTemplate.exchange(
            wiremockUrl,
            HttpMethod.DELETE,
            null,
            String.class
        );
		
        if (response.getStatusCode().is2xxSuccessful()) {
        	apiMapper.deleteById(id);
        }else {
        	throw new RuntimeException("WireMock API 삭제 실패: " + response.getBody());
        }
		
		
	}
	
	
	@Override
	public void updateApi(int id, ApiDTO apiDto) {
		
		ApiDTO apiDTO = apiMapper.findById(id);
		String oldWiremockId = apiDTO.getMockWiremockId();
		String uri = extractUri(apiDto.getMockApiUrl());
		if(uri == null || uri.equals("")) {
			uri = "/";
		}
		if (oldWiremockId == null) {
            throw new RuntimeException("해당 ID에 대한 Mock API를 찾을 수 없습니다.");
        }
		
		Map<String, Object> requestBody = new HashMap<>();
		Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("method", apiDto.getMockApiRequestMethod());
        requestMap.put("urlPathPattern", uri);
        requestMap.put("queryParameters", parseQueryParams(extractQuery(apiDto.getMockApiUrl())));

        // `requestBody`가 존재하면 `bodyPatterns` 추가
        if (apiDto.getRequestBody() != null && !apiDto.getRequestBody().isEmpty()) {
        	requestMap.put("bodyPatterns", List.of(
                Map.of("equalToJson", apiDto.getRequestBody()) // JSON 비교
            ));
        }

        requestBody.put("request", requestMap);
        requestBody.put("response", Map.of(
                "status", apiDto.getResponseStatusCode(),
                "body", apiDto.getResponseBody(),
                "transformers", List.of("response-template")
            ));
        
        // WireMock에 API 등록 요청 보내기
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<Map> response = restTemplate.exchange(
        		WIREMOCK_ADMIN_URL,
                HttpMethod.POST,
                requestEntity,
                Map.class
            );

        String wiremockId = response.getBody().get("uuid").toString();
        apiDto.setMockWiremockId(wiremockId);
        apiDto.setMockId(id);
        apiMapper.updateById(apiDto);
		
        // 기존 데이터 삭제
		String wiremockUrl = WIREMOCK_ADMIN_URL +"/"+ oldWiremockId;
        restTemplate.exchange(
            wiremockUrl,
            HttpMethod.DELETE,
            null,
            String.class
        );
        // stub save
        HttpEntity<String> saveRequest = new HttpEntity<>("{}", headers);
        restTemplate.postForEntity(WIREMOCK_ADMIN_URL + "/save", saveRequest, String.class);
		
        
	}
	
	@Override
	public Map<String, Object> getMockData(int id) {
		ApiDTO apiDTO = apiMapper.findById(id);
		String wiremockId = apiDTO.getMockWiremockId();
		String wiremockUrl = WIREMOCK_ADMIN_URL + "/" +  wiremockId;
		ResponseEntity<Map> wiremockResponse = restTemplate.exchange(
	            wiremockUrl, HttpMethod.GET, null, Map.class
	        );
		
		Map<String, Object> response = new HashMap<>();
		response.put("api", apiDTO);
		response.put("wiremock", wiremockResponse);
		return response;
	}
	
	@Override
	public void updateMockData(int id) {
		ApiDTO apiDTO = apiMapper.findById(id);
		
		boolean isBypass = apiDTO.getMockResponseStatus();
		String oldWiremockId = apiMapper.findWireMockIdById(id);
		String wiremockUrl = WIREMOCK_ADMIN_URL + "/" +  oldWiremockId;
		
		// 기존 데이터 가져오기
		 ResponseEntity<Map> wiremockResponse = restTemplate.exchange(
			        wiremockUrl, HttpMethod.GET, null, Map.class
			    );
		// 기존 데이터 삭제
        restTemplate.exchange(
            wiremockUrl,
            HttpMethod.DELETE,
            null,
            String.class
        );
		 
		 Map<String, Object> stubData = wiremockResponse.getBody();
		 Map<String, Object> responseBody = (Map<String, Object>) stubData.get("response");
		 
		 if(isBypass) {
			 String apiUrl = apiDTO.getMockApiUrl();
			 String proxyUrl = extractDomain(apiUrl);
			 responseBody.put("proxyBaseUrl", proxyUrl);
		 }else {
			 responseBody.remove("proxyBaseUrl");
		 }
		 
		 stubData.put("response", responseBody);
		 
		 
		// WireMock에 API 등록 요청 보내기
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(stubData, headers); 
        
        ResponseEntity<Map> response = restTemplate.exchange(
        		WIREMOCK_ADMIN_URL,
                HttpMethod.POST,
                requestEntity,
                Map.class
            );

        String wiremockId = response.getBody().get("uuid").toString();
        apiDTO.setMockWiremockId(wiremockId);
        apiDTO.setMockId(id);
        apiMapper.updateById(apiDTO);
		
        
        // stub save
        HttpEntity<String> saveRequest = new HttpEntity<>("{}", headers);
        restTemplate.postForEntity(WIREMOCK_ADMIN_URL + "/save", saveRequest, String.class);
		
	}
	

}
