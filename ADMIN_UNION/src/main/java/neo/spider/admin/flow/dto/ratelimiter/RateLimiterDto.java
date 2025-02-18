package neo.spider.admin.flow.dto.ratelimiter;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RateLimiterDto {
    private long ratelimiterId;
    private long applicationId;

    // 0: global, 1: method, 2: personal
    private int type;
    private String url;
    private int limitForPeriod;
    private long limitRefreshPeriod;
    private long timeoutDuration;

    @Override
    public String toString() {
        return "RateLimiterDto{" +
                "ratelimiterId=" + ratelimiterId +
                ", applicationId=" + applicationId +
                ", type=" + type +
                ", url='" + url + '\'' +
                ", limitForPeriod=" + limitForPeriod +
                ", limitRefreshPeriod=" + limitRefreshPeriod +
                ", timeoutDuration=" + timeoutDuration +
                '}';
    }
}
