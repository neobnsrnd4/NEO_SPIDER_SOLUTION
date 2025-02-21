package neo.spider.admin.flow.dto.ratelimiter;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RateLimiterSearchDto {
    private long ratelimiterId;
    private String applicationName;
    private int type;
    private String url;
    private int limitForPeriod;
    private long limitRefreshPeriod;
    private long timeOutDuration;
}
