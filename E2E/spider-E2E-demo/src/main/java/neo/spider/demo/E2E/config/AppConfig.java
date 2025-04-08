package neo.spider.demo.E2E.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = false)
@ComponentScan(basePackages = "neo.spider.solution.E2E") // logging-library 패키지도 스캔
public class AppConfig {
}
