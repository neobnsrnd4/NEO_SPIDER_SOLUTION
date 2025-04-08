package neo.spider.solution.E2E.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EnableLoggingConfig {

	private static boolean filterLogging;
	private static boolean controllerLogging;
	private static boolean serviceLogging;
	private static boolean sqlLogging;
	private static boolean dalLogging;
	
	@Value("${spider.e2e.logging.enable.filter:true}")
	public void setFilterLogging(boolean filterLogging) {
		EnableLoggingConfig.filterLogging = filterLogging;
	}
	
	@Value("${spider.e2e.logging.enable.controller:true}")
	public void setControllerLogging(boolean controllerLogging) {
		EnableLoggingConfig.controllerLogging = controllerLogging;
	}
	
	@Value("${spider.e2e.logging.enable.service:true}")
	public void setServiceLogging(boolean serviceLogging) {
		EnableLoggingConfig.serviceLogging = serviceLogging;
	}
	
	@Value("${spider.e2e.logging.enable.SQL:true}")
	public void setSQLLogging(boolean sqlLogging) {
		EnableLoggingConfig.sqlLogging = sqlLogging;
	}
	
	@Value("${spider.e2e.logging.enable.DAL:true}")
	public void setDalLogging(boolean dalLogging) {
		EnableLoggingConfig.dalLogging = dalLogging;
	}
	
	public static boolean getFilterLogging() {
		return filterLogging;
	}
	
	public static boolean getControllerLogging() {
		return controllerLogging;
	}
	
	public static boolean getServiceLogging() {
		return serviceLogging;
	}
	
	public static boolean getSQLLogging() {
		return sqlLogging;
	}
	
	public static boolean getDalLogging() {
		return dalLogging;
	}
}
