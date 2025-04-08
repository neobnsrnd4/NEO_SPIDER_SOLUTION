package neo.spider.demo.E2E.mybatis;

import java.sql.Statement;
import java.util.Properties;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


@Intercepts({
	@Signature(type = StatementHandler.class, method = "query", args = { Statement.class, ResultHandler.class }),
	@Signature(type = StatementHandler.class, method = "update", args = { Statement.class }),
	@Signature(type = StatementHandler.class, method = "batch", args = { Statement.class }),
	})
@Profile("mybatis")
@Component
public class ProjectMyBatisInterceptor implements Interceptor{
	private static final Logger log = LoggerFactory.getLogger(ProjectMyBatisInterceptor.class);
	
	
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Object result = null;
		try {
			// 실제 쿼리 실행
			result = invocation.proceed();
		} catch (Exception e){
			throw e;
		} finally {

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
