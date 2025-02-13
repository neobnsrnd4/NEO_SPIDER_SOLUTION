package neo.spider.admin.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import neo.spider.admin.common.interceptor.SessionInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	// /static/assets를 읽어오지 못하길래 아래와 같이 명시해줌
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/");
    }

    
    private final SessionInterceptor sessionInterceptor;

    public WebConfig(SessionInterceptor sessionInterceptor) {
        this.sessionInterceptor = sessionInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor)
        .addPathPatterns("/**")  // 인증을 체크할 URL 패턴
        .excludePathPatterns(
                "/**/*.html",  	// 모든 static .html 요청 제회(ui샘플봐야해서)
                "/signin.html",  // 로그인 페이지.html
                "/error",  		 // 에러 페이지
                "/signin",         // 로그인 URL
                "/signup",        // 사용자등록페이지.
                "/registerUser",         // 사용자등록처리.
                "/logout",         // 로그아웃.
                "/loginProcess",  // 로그인처리.
                "/**/*.css",        // CSS 파일
                "/**/*.js",         // JavaScript 파일
                "/**/images/**"      // 이미지 파일
        );
    }
    
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("http://localhost:4200", "*")
//                .allowedHeaders("*")
//                .allowedMethods("*")
//                .allowCredentials(true);
//    }
}
