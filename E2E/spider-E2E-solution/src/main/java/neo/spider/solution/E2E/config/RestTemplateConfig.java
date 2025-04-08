package neo.spider.solution.E2E.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import neo.spider.solution.E2E.RestTemplateInterceptor;

@Configuration
@ConditionalOnProperty(prefix = "spider.resttemplate", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RestTemplateConfig {

	@Bean
	public RestTemplateBuilder restTemplateBuilder() {
		return new RestTemplateBuilder().additionalInterceptors(new RestTemplateInterceptor());
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

}
