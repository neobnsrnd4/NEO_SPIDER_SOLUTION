//package neo.spider.admin.common.configuration;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.session.SessionRegistryImpl;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.session.HttpSessionEventPublisher;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
//        http.csrf(csrf -> csrf.ignoringRequestMatchers("/saveMsg", "/public/**", "/api/**", "/data-api/**"))
//            .formLogin(loginConfigurer -> loginConfigurer
//                .loginPage("/signin")
//                .loginProcessingUrl("/perform_login")
//                .defaultSuccessUrl("/index_page", true)
//                .usernameParameter("username") // 사용자 ID 필드
//                .passwordParameter("password") // 사용자 비밀번호 필드
//                .failureUrl("/signin?error=true").permitAll()
//            )
//            .logout(logoutConfigurer -> logoutConfigurer
//                .logoutSuccessUrl("/signin?logout=true")
//                .deleteCookies("JSESSIONID")
//                .invalidateHttpSession(true)
//                .clearAuthentication(true).permitAll()
//            )
//            .authorizeHttpRequests(requests -> requests
//                .requestMatchers("/dashboard", "/signin", "/login", "/public/**", "/assets/**").permitAll()
//                .requestMatchers("/admin/**").hasRole("ADMIN")
//                .requestMatchers("/student/**").hasRole("STUDENT")
//                .requestMatchers("/api/**", "/data-api/**", "/updateProfile", "/displayProfile").authenticated()
//            )
//            .httpBasic(Customizer.withDefaults());
//
//        http.sessionManagement(sessionManagement -> sessionManagement
//            .sessionConcurrency(sessionConcurrency -> sessionConcurrency
//                .maximumSessions(1)
//                .expiredUrl("/signin?expired=true")
//                .sessionRegistry(sessionRegistry())
//            )
//        );
//
//        return http.build();
//    }
//
//    @Bean
//    SessionRegistryImpl sessionRegistry() {
//        return new SessionRegistryImpl();
//    }
//
//    @Bean
//    HttpSessionEventPublisher httpSessionEventPublisher() {
//        return new HttpSessionEventPublisher();
//    }
//
//    @Bean
//    PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
//        return authConfig.getAuthenticationManager();
//    }
//}