package neo.spider.admin.flow.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigDto {
    private long id;
    private String name;
    private String appName;
    private int maxConcurrentCalls;
    private int limitForPeriod;
    private long limitRefreshPeriod;
    private long timeoutDuration;

    @Override
    public String toString() {
        return "ConfigDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", appName='" + appName + '\'' +
                ", maxConcurrentCalls=" + maxConcurrentCalls +
                ", limitForPeriod=" + limitForPeriod +
                ", limitRefreshPeriod=" + limitRefreshPeriod +
                ", timeoutDuration=" + timeoutDuration +
                '}';
    }
}