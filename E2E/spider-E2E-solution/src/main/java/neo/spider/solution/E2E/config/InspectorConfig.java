package neo.spider.solution.E2E.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import neo.spider.solution.E2E.CompositeStatementInspector;
import neo.spider.solution.E2E.JPALoggingInspector;

@Configuration
public class InspectorConfig {
	
	@Bean
	public CompositeStatementInspector compositeStatementInspector() {
		CompositeStatementInspector inspector = new CompositeStatementInspector();
		inspector.addInspector(new JPALoggingInspector());
		return inspector;
	}
}
