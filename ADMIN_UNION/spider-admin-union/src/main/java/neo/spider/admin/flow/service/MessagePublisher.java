package neo.spider.admin.flow.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@ConditionalOnProperty(name = "flow.control.enabled", havingValue = "true")
@Service
public class MessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    public MessagePublisher(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void publish(String topic, String message){
        redisTemplate.convertAndSend(topic, message);
    }
}
