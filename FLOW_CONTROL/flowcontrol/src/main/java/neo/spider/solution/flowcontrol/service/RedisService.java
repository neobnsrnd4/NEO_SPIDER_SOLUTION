package neo.spider.solution.flowcontrol.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	private String defaultValue = "50";

	@CachePut(key = "#key", value = "value")
	public String setStringValue(String key, String value) {
		redisTemplate.opsForValue().set(key, value);
		return value;
	}

	@Cacheable(key = "#key", value = "value")
	public String getStringValue(String key) {
		String value = (String) redisTemplate.opsForValue().get(key);
		// 키가 없는 경우 디폴트 50
		if (value == null) {
			value = defaultValue;
			setStringValue(key, value);
		}
		return value;
	}

	public Boolean deleteStringValue(String key) {
		return redisTemplate.delete(key);
	}

}
