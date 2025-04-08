package neo.spider.solution.E2E;

import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import neo.spider.solution.E2E.config.EnableLoggingConfig;
import neo.spider.solution.E2E.constants.MDCKeys;


@Profile("jpa")
@Component
public class JPALoggingInspector implements StatementInspector{

	@Override
	public String inspect(String sql) {
		if(EnableLoggingConfig.getSQLLogging()) {
			String Msql = sql.replaceAll("[a-z][0-9_]+(\\.|\\s)", "");
			MDC.put(MDCKeys.QUERY, Msql);
		}
		
//		MDC.put(MDCKeys.CURRENT, "JPARepository");
		return sql;
	}
	
	
}
