//package com.neobns.accounts.commons;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.slf4j.MDC;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//
//@Aspect
//@Component
//@Order(1)
//public class DemoAspect {
//	private static final Logger logger = LoggerFactory.getLogger(DemoAspect.class);
//
//	/**
//	 * Controller 계층의 메서드 로깅
//	 */
//	@Around("execution(* com.neobns.accounts.controller..*(..))")
//	public Object logControllerLayer(ProceedingJoinPoint joinPoint) throws Throwable {
//		return logExecution(joinPoint, "Controller");
//	}
//
//	/**
//	 * Service 계층의 메서드 로깅
//	 */
//	@Around("execution(* com.neobns.accounts.service..*(..))")
//	public Object logServiceLayer(ProceedingJoinPoint joinPoint) throws Throwable {
//		return logExecution(joinPoint, "Service");
//	}
//
//	/**
//	 * MyBatis Mapper 계층의 메서드 로깅
//	 */
//	@Around("execution(* com.neobns.accounts.mapper..*(..))")
//	public Object logMapperLayer(ProceedingJoinPoint joinPoint) throws Throwable {
//		return logExecution(joinPoint, "Mapper");
//	}
//
//	@Around("execution(* org.springframework.data.jpa.repository.JpaRepository+.*(..))")
//	public Object logJPAQueries(ProceedingJoinPoint joinPoint) throws Throwable {
//
//		return logExecution(joinPoint, "JPARepository");
//
//	}
//
//	/**
//	 * 공통 실행 로깅 메서드
//	 */
//	private Object logExecution(ProceedingJoinPoint joinPoint, String layer) throws Throwable {
//		logger.info("프로젝트의 AOP가 가로챈 실행 위치 : {}", layer);
//
//		Object result;
//		try {
//			result = joinPoint.proceed(); // 실제 메서드 실행
//		} catch (Exception e) {
//			throw e;
//		} finally {
//		}
//
//		return result;
//	}
//}
//


