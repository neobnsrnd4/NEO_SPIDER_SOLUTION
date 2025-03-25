package neo.spider.solution.batch.config;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DBProperties {
	
	private final Environment environment;
	
	public String getBatchUrl() {
		return environment.getProperty("spider.batch.datasource-batch.url");
	}
	
	public String getCommonUrl() {
		return environment.getProperty("spider.batch.datasource-common.url");
	}
	
	public String getTargetUrl() {
		return environment.getProperty("spider.batch.datasource-target.url");
	}
	
	public String getSpiderUrl() {
		return environment.getProperty("spider.batch.datasource-spiderdb.url");
	}
}
