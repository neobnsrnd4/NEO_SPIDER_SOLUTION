package neo.spider.admin.flow.dto.bulkhead;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BulkheadDto {
    private long bulkheadId;
    private long applicationId;
    private String url;
    private int maxConcurrentCalls;
    private int maxWaitDuration;

    @Override
    public String toString() {
        return "BulkheadDto{" +
                "bulkheadId=" + bulkheadId +
                ", applicationId=" + applicationId +
                ", url='" + url + '\'' +
                ", maxConcurrentCalls=" + maxConcurrentCalls +
                ", maxWaitDuration=" + maxWaitDuration +
                '}';
    }
}
