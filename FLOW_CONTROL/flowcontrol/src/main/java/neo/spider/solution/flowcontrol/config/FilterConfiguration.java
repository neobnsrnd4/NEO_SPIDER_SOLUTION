package neo.spider.solution.flowcontrol.config;

import neo.spider.solution.flowcontrol.filter.bulkhead.BulkheadFilter;
import neo.spider.solution.flowcontrol.filter.ratelimiter.GlobalRateLimiterFilter;
import neo.spider.solution.flowcontrol.filter.ratelimiter.DetailRateLimiterFilter;
import neo.spider.solution.flowcontrol.filter.ratelimiter.PersonalRateLimiterFilter;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import neo.spider.solution.flowcontrol.trie.TrieRegistry;

@Configuration
public class FilterConfiguration {

    private final BulkheadRegistry bulkheadRegistry;
    private final RateLimiterRegistry rateLimiterRegistry;
    private final TrieRegistry trieRegistry;

    public FilterConfiguration(BulkheadRegistry bulkheadRegistry,
                               RateLimiterRegistry rateLimiterRegistry,
                               TrieRegistry trieRegistry) {
        this.bulkheadRegistry = bulkheadRegistry;
        this.rateLimiterRegistry = rateLimiterRegistry;
        this.trieRegistry = trieRegistry;
    }

    @Bean
    public FilterRegistrationBean<BulkheadFilter> bulkheadFilter() {
        FilterRegistrationBean<BulkheadFilter> registrationBean = new FilterRegistrationBean<>(new BulkheadFilter(bulkheadRegistry, trieRegistry));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(104);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<GlobalRateLimiterFilter> globalRateLimiterFilter() {
        FilterRegistrationBean<GlobalRateLimiterFilter> registrationBean = new FilterRegistrationBean<>(new GlobalRateLimiterFilter(rateLimiterRegistry));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(101);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<DetailRateLimiterFilter> detailRateLimiterFilter() {
        FilterRegistrationBean<DetailRateLimiterFilter> registrationBean = new FilterRegistrationBean<>(new DetailRateLimiterFilter(rateLimiterRegistry, trieRegistry));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(102);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<PersonalRateLimiterFilter> personalRateLimiterFilter(){
        FilterRegistrationBean<PersonalRateLimiterFilter> registrationBean = new FilterRegistrationBean<>(new PersonalRateLimiterFilter(rateLimiterRegistry));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(103);
        return registrationBean;
    }
}