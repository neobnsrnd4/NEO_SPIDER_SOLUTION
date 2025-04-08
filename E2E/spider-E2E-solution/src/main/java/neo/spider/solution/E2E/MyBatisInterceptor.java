package neo.spider.solution.E2E;

import java.sql.Statement;
import java.util.Properties;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import neo.spider.solution.E2E.config.EnableLoggingConfig;
import neo.spider.solution.E2E.config.QueryThresholdConfig;
import neo.spider.solution.E2E.constants.MDCKeys;


@Intercepts({
	@Signature(type = StatementHandler.class, method = "query", args = { Statement.class, ResultHandler.class }),
	@Signature(type = StatementHandler.class, method = "update", args = { Statement.class }),
	@Signature(type = StatementHandler.class, method = "batch", args = { Statement.class }),
	})
@Profile("mybatis")
@Component
public class MyBatisInterceptor implements Interceptor{
//	private static final Logger log = LoggerFactory.getLogger(MyBatisInterceptor.class);
	
	
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		if(!EnableLoggingConfig.getSQLLogging()) {
			return invocation.proceed();
		}
		// 시작 시간 측정
		StatementHandler handler = (StatementHandler) invocation.getTarget();
		Long start = System.currentTimeMillis();
		String query = handler.getBoundSql().getSql().replaceAll("\\s+", " ").trim();
		
		long threshold = QueryThresholdConfig.getQueryThresholdMs();
		
		String called = MDC.get(MDCKeys.CURRENT);
		MDC.put(MDCKeys.CALL_TIMESTAMP, start.toString());
		MDC.put(MDCKeys.QUERY, query);
		MDC.put(MDCKeys.CALLED_BY, called);
		
		Object result = null;
		try {
			// 실제 쿼리 실행
			result = invocation.proceed();
		} catch (Exception e){
//			log.info("쿼리 실행 에러");
			MDC.put(MDCKeys.ERROR, e.getMessage());
			throw e;
		} finally {
			// 종료 시간 측정
			Long afterTime = System.currentTimeMillis();
			Long elapsedTime = afterTime - start;
			MDC.put(MDCKeys.CALL_TIMESTAMP, afterTime.toString());
			MDC.put(MDCKeys.EXECUTION_TIME, elapsedTime.toString());
			
			// 설정 시간보다 느리면 slow 로깅
			if (elapsedTime > threshold) {
				MDC.put(MDCKeys.DELAY, "DataBase Access Delayed!!!!");
			}
//			log.info("마이바티스 인터셉터에서...... 이전 호출: [{}], 실행 시간 : [{}] , 기준 시간 : [{}] ", called, elapsedTime, threshold);
			LoggingUtils.log(null);
			
			MDC.remove(MDCKeys.DELAY);
			MDC.remove(MDCKeys.ERROR);
			MDC.remove(MDCKeys.QUERY);
			MDC.remove(MDCKeys.EXECUTION_TIME);
		}
		return result;
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		// 필요 시 프로퍼티 설정
	}
}
