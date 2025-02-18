package neo.spider.solution.apimock.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.github.tomakehurst.wiremock.WireMockServer;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import neo.spider.solution.apimock.dto.ApiDTO;
import neo.spider.solution.apimock.dto.ChangeModeRequest;
import neo.spider.solution.apimock.service.ApiService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiController {
	
	private final ApiService apiService;
	private final WireMockServer wireMockServer;
	
	@PostMapping("/toggle-mode")
	public ResponseEntity<String> toggleApiMode(@RequestBody Integer id) {
		try {
			apiService.toggleResponseStatusById(id);
			// wiremock stub update
			apiService.updateMockData(id);
	        return ResponseEntity.status(HttpStatus.OK).body("Successfully updated the response status.");
	    } catch (Exception e) {
	    	e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update response status.");
	    }
	}
	
	@PostMapping("/change-mode-selected")
	public ResponseEntity<String> changeMode(@RequestBody ChangeModeRequest request) {
		try {
			apiService.changeModeByIds(request.getIds(), request.isTargetMode());
	        return ResponseEntity.status(HttpStatus.OK).body("Successfully updated the response status.");
	    } catch (Exception e) {
	    	e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update response status.");
	    }
	}
	
	@PostMapping("/health-check")
	public ResponseEntity<String> healthCheck(@RequestBody Integer id) {
		try {
			apiService.performHealthCheck(id);
	        return ResponseEntity.status(HttpStatus.OK).body("Successfully performed Health Check.");
	    } catch (Exception e) {
	    	e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to perform Health Check.");
	    }
	}
	
	@GetMapping("/execute/{id}")
	public void executeApi(@PathVariable int id, HttpServletResponse response) throws IOException, URISyntaxException {
		ApiDTO apiDTO = apiService.getApi(id);
		URI uri = new URI(apiDTO.getMockApiUrl());		
		String apiUri = uri.getPath();
		if(uri.getQuery() != null) {
			apiUri = apiUri + "?" + uri.getQuery();
		}
		
		apiDTO = apiService.performHealthCheck(id);
		String apiUrl = apiDTO.getMockApiUrl();
		boolean isMockMode = !apiDTO.getMockResponseStatus();
		String redirectUrl;
		if(isMockMode) {
			redirectUrl = "http://localhost:" + wireMockServer.port() + apiUri;
		}else {
			redirectUrl = apiUrl;
		}
		// apiDTO.getMockResponseStatus() 1이면 by-pass 0이면 stub
				
		response.sendRedirect(redirectUrl);
	}
	
	@PostMapping("/add")
	public ResponseEntity<String> addApi(@RequestBody ApiDTO apiDTO) {
	   try {
		   apiService.saveNewApi(apiDTO);
	        return ResponseEntity.status(HttpStatus.CREATED).body("{\"ok\" : \"API 등록 성공\"}");
	   } catch (Exception e) {
	    	e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\" : \"API 등록 실패\"}");
	    }
        	
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteApi(@PathVariable int id){
		
		try {
			apiService.deleteApi(id);
	        return ResponseEntity.status(HttpStatus.OK).body("{\"ok\" : \"API 삭제 성공\"}");
	    } catch (Exception e) {
	    	e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\" : \"API 삭제 실패\"}");
	    }
		
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<String> updateMockApi(@PathVariable int id, @RequestBody ApiDTO apiDto) {
        try {
        	apiService.updateApi(id, apiDto);
            return ResponseEntity.status(HttpStatus.OK).body("{\"ok\" : \"API 수정 성공\"}");
		} catch (Exception e) {
			e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\" : \"API 수정 실패\"}");
		}
		
    }
	
	@GetMapping("/get/{id}")
	 public ResponseEntity<Map<String, Object>> getMockApiById(@PathVariable int id) {
        ApiDTO apiDto = apiService.getApi(id); 
        Map<String, Object> response = apiService.getMockData(id);
        if (apiDto == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response);
    }
	
	
}
