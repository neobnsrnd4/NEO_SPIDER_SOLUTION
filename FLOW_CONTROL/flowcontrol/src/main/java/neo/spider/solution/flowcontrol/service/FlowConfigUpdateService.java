package neo.spider.solution.flowcontrol.service;

import java.time.Duration;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;
import neo.spider.solution.flowcontrol.ConfigurationProp;
import neo.spider.solution.flowcontrol.dto.BulkheadDto;
import neo.spider.solution.flowcontrol.dto.RateLimiterDto;
import neo.spider.solution.flowcontrol.dto.UpdateConfigDto;
import neo.spider.solution.flowcontrol.filter.ratelimiter.FilterManager;
import neo.spider.solution.flowcontrol.mapper.BulkheadMapper;
import neo.spider.solution.flowcontrol.mapper.RateLimiterMapper;
import neo.spider.solution.flowcontrol.trie.TrieRegistry;

@Service
public class FlowConfigUpdateService {
	private final BulkheadRegistry bulkheadRegistry;
	private final RateLimiterRegistry rateLimiterRegistry;
	private final ObjectMapper objectMapper;
	private final TrieRegistry trieRegistry;
	private final RedisService redisService;
	private final ConfigurationProp prop;
	private final FilterManager filterManager;

	public FlowConfigUpdateService(BulkheadRegistry bulkheadRegistry, RateLimiterRegistry rateLimiterRegistry,
			ObjectMapper objectMapper, BulkheadMapper bulkheadMapper, RateLimiterMapper rateLimiterMapper,
			TrieRegistry trieRegistry, RedisService redisService, ConfigurationProp prop, FilterManager filterManager) {
		this.bulkheadRegistry = bulkheadRegistry;
		this.rateLimiterRegistry = rateLimiterRegistry;
		this.objectMapper = objectMapper;
		this.trieRegistry = trieRegistry;
		this.redisService = redisService;
		this.prop = prop;
		this.filterManager = filterManager;
	}

	public void updateConfig(String message) {
		try {
			UpdateConfigDto updateConfigDto = objectMapper.readValue(message, UpdateConfigDto.class);

			// ratelimiter mode toggle
			String mode = updateConfigDto.getRateLimiter().getRatelimiterMode().trim();
			String applicationName = prop.getApplication().getName();
			if (mode != null) {
				String toggleResilienceKey = applicationName + "/filterManager/resilience4j";
				String toggleBucket4jKey = applicationName + "/filterManager/bucket4j";
				String group1 = prop.getFilters().getGroup1();
				String group2 = prop.getFilters().getGroup2();
				if (mode.equals("bucket4j")) {
					redisService.setStringValue(toggleBucket4jKey, "true");
					redisService.setStringValue(toggleResilienceKey, "false");
					filterManager.enableGroup(group2);
					filterManager.disableGroup(group1);

				} else if (mode.equals("resilience4j")) {
					redisService.setStringValue(toggleBucket4jKey, "false");
					redisService.setStringValue(toggleResilienceKey, "true");
					filterManager.enableGroup(group1);
					filterManager.disableGroup(group2);
				}
				return;
			}

			int doing = updateConfigDto.getDoing();
			if (updateConfigDto.getType() == 0) {
				// bulkhead
				String url = updateConfigDto.getName();
				BulkheadDto bulkheadDto = updateConfigDto.getBulkhead();
				if (doing == 0) {
//                    BulkheadDto configDto = bulkheadMapper.findById(id);
					BulkheadConfig newConfig = BulkheadConfig.custom()
							.maxConcurrentCalls(bulkheadDto.getMaxConcurrentCalls())
							.maxWaitDuration(Duration.ofSeconds(bulkheadDto.getMaxWaitDuration())).build();

					if (bulkheadRegistry.find(url).isPresent()) {
						// update
						bulkheadRegistry.bulkhead(url).changeConfig(newConfig);
					} else {
						// create
						bulkheadRegistry.bulkhead(url, newConfig);
						trieRegistry.getBulkheadTrie().insert(url);
					}
				} else {
					// delete
					if (bulkheadRegistry.find(url).isPresent()) {
						bulkheadRegistry.remove(url);
						trieRegistry.getBulkheadTrie().delete(url);
					}
				}
			} else if (updateConfigDto.getType() == 1) {
				// rateLimiter
				String url = updateConfigDto.getName();
				RateLimiterDto rateLimiterDto = updateConfigDto.getRateLimiter();

				if (doing == 0) {
					RateLimiterConfig newConfig = RateLimiterConfig.custom()
							.limitForPeriod(rateLimiterDto.getLimitForPeriod())
							.limitRefreshPeriod(java.time.Duration.ofSeconds(rateLimiterDto.getLimitRefreshPeriod()))
							.timeoutDuration(java.time.Duration.ofSeconds(rateLimiterDto.getTimeoutDuration())).build();

					if (rateLimiterRegistry.find(url).isPresent()) {
						// update
						RateLimiter newRateLimiter = RateLimiter.of(url, newConfig);
						rateLimiterRegistry.replace(url, newRateLimiter);
					} else {
						// create
						rateLimiterRegistry.rateLimiter(url, newConfig);
						trieRegistry.getRateLimiterTrie().insert(url);
					}
				} else {
					// delete
					if (rateLimiterRegistry.find(url).isPresent()) {
						rateLimiterRegistry.remove(url);
						trieRegistry.getRateLimiterTrie().delete(url);
					}
				}

			} else if (updateConfigDto.getType() == 3) {
				// redis rateLimiter
				String url = updateConfigDto.getName();
				RateLimiterDto rateLimiterDto = updateConfigDto.getRateLimiter();

				// rateLimiterDto.getType(). 0: global , 1: detail(url) , 2: personal
				String slashOrNoneString = rateLimiterDto.getType() == 1 ? "" : "/";
				String capacityKey = applicationName + "/capacity/" + rateLimiterDto.getType() + slashOrNoneString
						+ rateLimiterDto.getUrl();
				String refillKey = applicationName + "/refill/" + rateLimiterDto.getType() + slashOrNoneString
						+ rateLimiterDto.getUrl();
				;

				if (doing == 0) {
					// update / create
					// redis bucket4j 적용: 기존 레디스 버킷 기준 변경

					long capacity = rateLimiterDto.getLimitForPeriod();
					long refill = rateLimiterDto.getLimitRefreshPeriod();
					redisService.setStringValue(capacityKey, String.valueOf(capacity));
					redisService.setStringValue(refillKey, String.valueOf(refill));

					System.out.println("capacity, refill : " + capacity + " : " + refill);
				} else {
					// delete
					redisService.deleteStringValue(capacityKey);
					redisService.deleteStringValue(refillKey);

				}

			}

		} catch (Exception e) {
			throw new Error(e.getMessage());
//			System.out.println(e.getMessage());
		}
	}

}