package neo.spider.solution.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AdminInterceptor())
                .addPathPatterns("/admin/**") // `/admin` 요청만 인터셉트
                .excludePathPatterns("/login", "/logout"); // 로그인, 로그아웃은 제외
    }
}

class AdminInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession();
        Boolean isAdmin = (Boolean) session.getAttribute("adminUser");

        if (isAdmin == null || !isAdmin) {
            response.sendRedirect("/login"); // 로그인 페이지로 리다이렉트
            return false;
        }
        return true;
    }
}
