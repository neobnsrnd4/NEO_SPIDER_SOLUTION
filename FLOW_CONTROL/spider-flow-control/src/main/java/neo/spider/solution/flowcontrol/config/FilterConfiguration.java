package neo.spider.solution.flowcontrol.config;

import neo.spider.solution.flowcontrol.ConfigurationProp;
import neo.spider.solution.flowcontrol.filter.bulkhead.BulkheadFilter;
import neo.spider.solution.flowcontrol.filter.ratelimiter.GlobalRateLimiterFilter;
import neo.spider.solution.flowcontrol.filter.ratelimiter.DetailRateLimiterFilter;
import neo.spider.solution.flowcontrol.filter.ratelimiter.FilterManager;
import neo.spider.solution.flowcontrol.filter.ratelimiter.PersonalRateLimiterFilter;
import neo.spider.solution.flowcontrol.filter.ratelimiter.redis.RedisDetailRateLimiterFilter;
import neo.spider.solution.flowcontrol.filter.ratelimiter.redis.RedisGlobalRateLimiterFilter;
import neo.spider.solution.flowcontrol.filter.ratelimiter.redis.RedisPersonalRateLimiterFilter;
import neo.spider.solution.flowcontrol.service.JwtProviderService;
import neo.spider.solution.flowcontrol.service.RedisRateLimiterService;
import neo.spider.solution.flowcontrol.service.RedisService;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import neo.spider.solution.flowcontrol.trie.TrieRegistry;

@Configuration
public class FilterConfiguration {

	private final FilterManager filterManager;
	private final ConfigurationProp prop;
	private final String groupName1;
	private final String groupName2;
	private final JwtProviderService jwtProviderService;
	private final BulkheadRegistry bulkheadRegistry;
	private final RateLimiterRegistry rateLimiterRegistry;
	private final TrieRegistry trieRegistry;
	private final RedisRateLimiterService redisRateLimiterService;
	private final RedisService redisService;

	public FilterConfiguration(FilterManager filterManager, ConfigurationProp prop,
			JwtProviderService jwtProviderService, BulkheadRegistry bulkheadRegistry,
			RateLimiterRegistry rateLimiterRegistry, TrieRegistry trieRegistry,
			RedisRateLimiterService redisRateLimiterService, RedisService redisService) {
		this.filterManager = filterManager;
		this.prop = prop;
		this.groupName1 = prop.getFilters().getGroup1();
		this.groupName2 = prop.getFilters().getGroup2();
		this.jwtProviderService = jwtProviderService;
		this.bulkheadRegistry = bulkheadRegistry;
		this.rateLimiterRegistry = rateLimiterRegistry;
		this.trieRegistry = trieRegistry;
		this.redisRateLimiterService = redisRateLimiterService;
		this.redisService = redisService;
	}

	@Bean
	public FilterRegistrationBean<BulkheadFilter> bulkheadFilter() {
		FilterRegistrationBean<BulkheadFilter> registrationBean = new FilterRegistrationBean<>(
				new BulkheadFilter(bulkheadRegistry, trieRegistry));
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(104);
		return registrationBean;
	}

	@Bean
	public FilterRegistrationBean<GlobalRateLimiterFilter> globalRateLimiterFilter() {
		FilterRegistrationBean<GlobalRateLimiterFilter> registrationBean = new FilterRegistrationBean<>(
				new GlobalRateLimiterFilter(filterManager, prop, rateLimiterRegistry));
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(101);
		System.out.println("global rate limiter 등록");
		return registrationBean;
	}

	@Bean
	public FilterRegistrationBean<DetailRateLimiterFilter> detailRateLimiterFilter() {
		FilterRegistrationBean<DetailRateLimiterFilter> registrationBean = new FilterRegistrationBean<>(
				new DetailRateLimiterFilter(filterManager, prop, rateLimiterRegistry, trieRegistry));
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(102);
		System.out.println("detail rate limiter 등록");
		return registrationBean;
	}

	@Bean
	public FilterRegistrationBean<PersonalRateLimiterFilter> personalRateLimiterFilter() {
		FilterRegistrationBean<PersonalRateLimiterFilter> registrationBean = new FilterRegistrationBean<>(
				new PersonalRateLimiterFilter(filterManager, prop, rateLimiterRegistry));
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(103);
		System.out.println("personal rate limiter 등록");
		return registrationBean;
	}
	
	// 레디스 필터

	@Bean
	public FilterRegistrationBean<RedisGlobalRateLimiterFilter> redisGlobalRateLimiterFilter() {
		FilterRegistrationBean<RedisGlobalRateLimiterFilter> registrationBean = new FilterRegistrationBean<>(
				new RedisGlobalRateLimiterFilter(filterManager, prop,  redisRateLimiterService, redisService));
		registrationBean.setOrder(104);
		registrationBean.addUrlPatterns("/*");
		System.out.println("redis global rate limiter 등록");
		return registrationBean;
	}
	
	@Bean
	public FilterRegistrationBean<RedisDetailRateLimiterFilter> redisDetailRateLimiterFilter() {
		FilterRegistrationBean<RedisDetailRateLimiterFilter> registrationBean = new FilterRegistrationBean<>(
				new RedisDetailRateLimiterFilter(filterManager, prop,  redisRateLimiterService, redisService));
		registrationBean.setOrder(105);
		registrationBean.addUrlPatterns("/*");
		System.out.println("redis detail(url) rate limiter 등록");
		return registrationBean;
	}
	
	@Bean
	public FilterRegistrationBean<RedisPersonalRateLimiterFilter> redisPersonalRateLimiterFilter() {
		FilterRegistrationBean<RedisPersonalRateLimiterFilter> registrationBean = new FilterRegistrationBean<>(
				new RedisPersonalRateLimiterFilter(filterManager, prop, jwtProviderService, redisRateLimiterService, redisService));
		registrationBean.setOrder(106);
		registrationBean.addUrlPatterns("/*");
		System.out.println("redis personal rate limiter 등록");
		return registrationBean;	
	}

}