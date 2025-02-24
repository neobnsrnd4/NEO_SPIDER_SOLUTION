//package neo.spider.demo.E2E.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import com.example.logging.RequestFilter;
//import com.neobns.accounts.filter.DemoFilter;
//
//@Configuration
//public class FilterConfig {
//	
//	@Autowired
//	public void modifyRequestFilterOrder(FilterRegistrationBean<RequestFilter> registrationBean) {
//		registrationBean.setOrder(50);
//	}
//	
//	//만약 필터를 재정의할 경우,
//	@Bean
//    public FilterRegistrationBean<DemoFilter> aFilterRegistrationBean() {
//        FilterRegistrationBean<DemoFilter> registrationBean = new FilterRegistrationBean<>();
//        registrationBean.setFilter(new DemoFilter());
//        registrationBean.setOrder(10); // A측 필터가 마지막에 실행됨
//        registrationBean.addUrlPatterns("/*");
//        return registrationBean;
//    }
//    
//	 
//}


