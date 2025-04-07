package neo.spider.solution.flowcontrol.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import neo.spider.solution.flowcontrol.ConfigurationProp;
import neo.spider.solution.flowcontrol.service.FlowConfigUpdateService;

@Configuration
public class RedisConfig {

	private final ConfigurationProp prop;

	public RedisConfig(ConfigurationProp prop) {
		this.prop = prop;
	}

	private RedisClient redisClient() {
		return RedisClient.create(RedisURI.builder().withHost(prop.getData().getRedis().getHost())
				.withPort(prop.getData().getRedis().getPort()).build());
	}

	@Bean
	public RedisMessageListenerContainer redsContainer(RedisConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter, MessageListenerAdapter deleteListener) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(listenerAdapter, new PatternTopic(prop.getApplication().getName()));
		return container;
	}

	@Bean
	public MessageListenerAdapter listenerAdapter(FlowConfigUpdateService service) {
		return new MessageListenerAdapter(service, "updateConfig");
	}

	// proxy manager가 redis 사용하며 이를 이용해 bucket 생성 가능
	@Bean
	public ProxyManager<String> lettuceBasedProxyManager() {
		RedisClient redisClient = redisClient();
		// key, value 각각 string, byte array로 직렬화
		StatefulRedisConnection<String, byte[]> redisConnection = redisClient
				.connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));

		LettuceBasedProxyManager<String> proxyManager = LettuceBasedProxyManager.builderFor(redisConnection)
				.withExpirationStrategy(
						ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofMinutes(1)))
				.build();

		return proxyManager;
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(factory);
		StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
		template.setKeySerializer(stringRedisSerializer);
		template.setHashKeySerializer(stringRedisSerializer);
		template.setHashValueSerializer(stringRedisSerializer);
		template.setValueSerializer(stringRedisSerializer);
		return template;
	}

}
