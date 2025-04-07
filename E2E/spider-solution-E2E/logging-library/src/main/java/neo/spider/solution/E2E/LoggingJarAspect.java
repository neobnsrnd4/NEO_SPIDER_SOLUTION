package neo.spider.solution.E2E;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import neo.spider.solution.E2E.config.EnableLoggingConfig;
import neo.spider.solution.E2E.config.QueryThresholdConfig;
import neo.spider.solution.E2E.constants.MDCKeys;

@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class LoggingJarAspect {
//	private static final Logger logger = LoggerFactory.getLogger(LoggingJarAspect.class);

	/**
	 * Controller 계층의 메서드 로깅
	 */
//	@Around("execution(* com.neobns.accounts.controller..*(..))")
	@Around("@within(org.springframework.stereotype.Controller) || @within(org.springframework.web.bind.annotation.RestController)")
	public Object logControllerLayer(ProceedingJoinPoint joinPoint) throws Throwable {
//		return logExecution(joinPoint, "Controller");
		return logExecution(joinPoint, 0);
	}

	/**
	 * Service 계층의 메서드 로깅
	 */
//	@Around("execution(* com.neobns.accounts.service..*(..))")
	@Around("@within(org.springframework.stereotype.Service)")
	public Object logServiceLayer(ProceedingJoinPoint joinPoint) throws Throwable {
//		return logExecution(joinPoint, "Service");
		return logExecution(joinPoint, 1);
	}

	/**
	 * MyBatis Mapper 계층의 메서드 로깅
	 */
//	@Around("execution(* com.neobns.accounts.mapper..*(..))")
	@Around("@within(org.apache.ibatis.annotations.Mapper)")
	public Object logMapperLayer(ProceedingJoinPoint joinPoint) throws Throwable {
//		return logExecution(joinPoint, "Mapper");
		return logExecution(joinPoint, 2);
	}

	@Around("execution(* org.springframework.data.jpa.repository.JpaRepository+.*(..))")
	public Object logJPAQueries(ProceedingJoinPoint joinPoint) throws Throwable {
		if (!EnableLoggingConfig.getSQLLogging())
			return joinPoint.proceed();
		// JPA Repository 호출 시점.
		Long start = System.currentTimeMillis();
		// 쿼리 속도 판단 기준
		long threshold = QueryThresholdConfig.getQueryThresholdMs();

		String reposName = joinPoint.getSignature().getDeclaringType().getSimpleName();
		String methodName = joinPoint.getSignature().getName();
		String called = MDC.get(MDCKeys.CURRENT);
		MDC.put(MDCKeys.CALL_TIMESTAMP, start.toString());
		MDC.put(MDCKeys.CALLED_BY, called);

		Object result = null;
		try {
			result = joinPoint.proceed();
		} catch (Exception e) {
			MDC.put(MDCKeys.ERROR, e.getMessage());
			System.out.println("[ jpa repository" + "] " + e.getMessage());
			throw e;
		} finally {
			Long afterTime = System.currentTimeMillis();
			Long elapsedTime = afterTime - start;

			MDC.put(MDCKeys.EXECUTION_TIME, elapsedTime.toString());
			MDC.put(MDCKeys.CALL_TIMESTAMP, afterTime.toString());
			if (elapsedTime > threshold) {
				MDC.put(MDCKeys.DELAY, "JPA Process Delayed!!!!!!");
			}

//			logger.info("JPA 실행 : [{}] ", reposName + methodName);
			LoggingUtils.log(null);

			MDC.remove(MDCKeys.DELAY);
			MDC.remove(MDCKeys.ERROR);
			MDC.remove(MDCKeys.QUERY);
			MDC.remove(MDCKeys.EXECUTION_TIME);
		}

		return result;

	}

	/**
	 * 공통 실행 로깅 메서드
	 */
	private Object logExecution(ProceedingJoinPoint joinPoint, int layer) throws Throwable {
//		if(!EnableLoggingConfig.getControllerLogging() && layer.equals("Controller")) {
		if (!EnableLoggingConfig.getControllerLogging() && layer == 0) {
			return joinPoint.proceed();
//		} else if (!EnableLoggingConfig.getServiceLogging() && layer.equals("Service")) {
		} else if (!EnableLoggingConfig.getServiceLogging() && layer == 1) {
			return joinPoint.proceed();
//		} else if (!EnableLoggingConfig.getDalLogging() && layer.equals("Mapper")) {
		} else if (!EnableLoggingConfig.getDalLogging() && layer==2) {
			return joinPoint.proceed();
		}

		Long start = System.currentTimeMillis();
//		String className = layer.equals("Mapper") ? joinPoint.getTarget().getClass().getInterfaces()[0].getName()
//				: joinPoint.getTarget().getClass().getName();
//		String className = layer.equals("Mapper") ? joinPoint.getTarget().getClass().getInterfaces()[0].getSimpleName()
		String className = layer==2 ? joinPoint.getTarget().getClass().getInterfaces()[0].getSimpleName()
				: joinPoint.getTarget().getClass().getSimpleName();

		String methodName = joinPoint.getSignature().getName();

//        MDC.put("methodName", methodName);
		String called = MDC.get(MDCKeys.CURRENT);
		MDC.put(MDCKeys.CALLED_BY, called);
		MDC.put(MDCKeys.CURRENT, className + "." + methodName);
		MDC.put(MDCKeys.CALL_TIMESTAMP, start.toString());
		// 메서드 실행 전 로깅
//		logger.info("입장 합니다. AOP현재 위치 : {}", className + "." + methodName);
		LoggingUtils.log(null);

		Object result;
		try {
			result = joinPoint.proceed(); // 실제 메서드 실행
		} catch (Exception e) {
			MDC.put(MDCKeys.ERROR, e.getMessage());
			System.out.println("[" + layer + "] " + e.getMessage());
			throw e;
		} finally {
			Long afterTime = System.currentTimeMillis();
			Long elapsedTime = afterTime - start;

			// called <-> current
			String after_calling = MDC.get(MDCKeys.CURRENT);
			MDC.put(MDCKeys.CALLED_BY, after_calling);
			MDC.put(MDCKeys.CURRENT, called);
			MDC.put(MDCKeys.EXECUTION_TIME, elapsedTime.toString());
			MDC.put(MDCKeys.CALL_TIMESTAMP, afterTime.toString());
//			if (layer.equals("Mapper") && !EnableLoggingConfig.getSQLLogging()
			if (layer==2 && !EnableLoggingConfig.getSQLLogging()
					&& elapsedTime > QueryThresholdConfig.getQueryThresholdMs()) {
				MDC.put(MDCKeys.DELAY, "Data Access Delayed");
			}
//			logger.info("빠져 나갑니다. AOP현재 위치 : {}", className + "." + methodName);
			LoggingUtils.log(null);

			MDC.remove(MDCKeys.ERROR);
			MDC.remove(MDCKeys.EXECUTION_TIME);
			MDC.remove(MDCKeys.DELAY);

		}

		return result;
	}
}