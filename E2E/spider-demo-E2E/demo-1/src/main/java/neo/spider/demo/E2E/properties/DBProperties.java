package neo.spider.demo.E2E.properties;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DBProperties {
	
	private final Environment environement;
	
	public String getMetaUrl() {
		return environement.getProperty("spring.datasource-meta.url");
	}
	
	public String getDataUrl() {
		return environement.getProperty("spring.datasource-data.url");
	}

	
	
}
