package neo.spider.solution.E2E.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import neo.spider.solution.E2E.RequestFilter;

@Configuration
@ConditionalOnMissingBean(name = "requestFilterRegistrationBean")
public class RequestFilterAutoConfiguration {

	@Bean
    public FilterRegistrationBean<RequestFilter> requestFilterRegistrationBean() {
        FilterRegistrationBean<RequestFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestFilter());
        // 기본 우선순위를 낮은 숫자로 지정하여 요청 초반에 실행하도록 함 (예: 10)
        registrationBean.setOrder(10);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
