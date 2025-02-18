package neo.spider.admin.flow.dto.bulkhead;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BulkheadSearchDto {
    private long bulkheadId;
    private String applicationName;
    private String url;
    private int maxConcurrentCalls;
    private int maxWaitDuration;
}
