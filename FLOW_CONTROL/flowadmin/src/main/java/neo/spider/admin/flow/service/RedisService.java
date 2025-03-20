package neo.spider.admin.flow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@CachePut(key = "#key", value = "value")
	public String setStringValue(String key, String value) {
		redisTemplate.opsForValue().set(key, value);
		return value;
	}

	@Cacheable(key = "#key", value = "value")
	public String getStringValue(String key) {
		String value = (String) redisTemplate.opsForValue().get(key);
		return value;
	}

}
