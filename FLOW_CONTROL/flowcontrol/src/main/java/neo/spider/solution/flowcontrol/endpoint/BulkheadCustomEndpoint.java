package neo.spider.solution.flowcontrol.endpoint;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;
import neo.spider.solution.flowcontrol.ConfigurationProp;

import java.util.HashMap;
import java.util.Map;

@Component
@Endpoint(id = "rooms")
public class BulkheadCustomEndpoint {

    private final BulkheadRegistry bulkheadRegistry;
    private final ConfigurationProp prop;

    public BulkheadCustomEndpoint(BulkheadRegistry bulkheadRegistry,
                                  ConfigurationProp prop) {
        this.bulkheadRegistry = bulkheadRegistry;
        this.prop = prop;
    }

    @ReadOperation
    public Map<String, Object> rooms() {
        Map<String, Object> result = new HashMap<>();

        Map<String, Integer> rooms = new HashMap<>();
        for (Bulkhead bulkhead : bulkheadRegistry.getAllBulkheads()) {
            String name = bulkhead.getName();
            Integer room = bulkhead.getMetrics().getAvailableConcurrentCalls();
            rooms.put(name, room);
        }
        result.put("application", prop.getName());
        result.put("rooms", rooms);

        return result;
    }
}
