package neo.spider.solution.flowcontrol.service;

import java.lang.module.ModuleDescriptor.Builder;
import java.time.Duration;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.TokensInheritanceStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.lettuce.core.Limit;

@Service
public class BucketRateLimiterService {

	private final ProxyManager<String> proxyManager;

	public BucketRateLimiterService(ProxyManager<String> proxyManager) {
		this.proxyManager = proxyManager;
	}

	public Bucket getBucket(String key, long capacity, long refill) {
		// bucket 설정
		Supplier<BucketConfiguration> supplier = getConfigSupplier((int) capacity, (int) refill);
		Bucket bucket = proxyManager.builder().build(key, supplier);
		return bucket;
	}

	public Bucket getReplacedBucket(String key, long capacity, long refill) {

		Supplier<BucketConfiguration> supplier = getConfigSupplier((int) capacity, (int) refill);

		Bucket bucket = proxyManager.builder().build(key, supplier);

		BucketConfiguration newConfiguration = BucketConfiguration.builder()
				.addLimit(Bandwidth.simple(capacity, Duration.ofSeconds(refill))).build();

		bucket.replaceConfiguration(newConfiguration, TokensInheritanceStrategy.RESET);
		
		System.out.println("replace bucket");
		return bucket;
	}

	public Supplier<BucketConfiguration> getConfigSupplier(int capacity, int refill) {

		return () -> BucketConfiguration.builder()
				.addLimit(limit -> limit.capacity(capacity).refillGreedy(capacity, Duration.ofSeconds(refill))).build();
	}

}
