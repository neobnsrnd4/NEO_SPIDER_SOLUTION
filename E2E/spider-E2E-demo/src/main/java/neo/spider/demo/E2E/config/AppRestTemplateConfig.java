package neo.spider.demo.E2E.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Configuration
public class AppRestTemplateConfig {
	
	private final RestTemplate restTemplate;
	
	public AppRestTemplateConfig(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	@PostConstruct
	public void addInterceptor() {
//		만약 프로젝트에서 재정의한 ClientHttpRequestInterceptor가 있다면 이곳에서 아래 코드 작성. 라이브러리의 interceptor가 먼저 동작하고,
//		아래에 추가한 순서대로 동작하게 됨.
//		restTemplate.getInterceptors().add();
//		
//		
//		만약 재정의한 ClientHttpRequestInterceptor들의 순서를 정하고 싶다면
		/*
		
		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new AInterceptor());  AInterceptor를 먼저 추가 (우선순위 높음)
        interceptors.add(new LibraryInterceptor());  LibraryInterceptor를 뒤에 추가 (우선순위 낮음)

         RestTemplate의 인터셉터 리스트를 직접 설정
        restTemplate.setInterceptors(interceptors);
      
        
		 */
	}
}


