package neo.spider.solution.flowcontrol.config;

import neo.spider.solution.flowcontrol.ConfigurationProp;
import neo.spider.solution.flowcontrol.service.FlowConfigUpdateService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisConfig {

    private final ConfigurationProp prop;

    public RedisConfig(ConfigurationProp prop) {
        this.prop = prop;
    }

    @Bean
    public RedisMessageListenerContainer redsContainer(RedisConnectionFactory connectionFactory,
                                                       MessageListenerAdapter listenerAdapter,
                                                       MessageListenerAdapter deleteListener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new PatternTopic(prop.getName()));
        return container;
    }
    @Bean
    public MessageListenerAdapter listenerAdapter(FlowConfigUpdateService service) {
        return new MessageListenerAdapter(service, "updateConfig");
    }

}
