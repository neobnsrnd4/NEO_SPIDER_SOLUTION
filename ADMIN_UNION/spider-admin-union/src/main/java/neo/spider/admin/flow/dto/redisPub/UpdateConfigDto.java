package neo.spider.admin.flow.dto.redisPub;

import lombok.Getter;
import lombok.Setter;
import neo.spider.admin.flow.dto.bulkhead.BulkheadDto;
import neo.spider.admin.flow.dto.ratelimiter.RateLimiterDto;

@Getter
@Setter
public class UpdateConfigDto {
    //0 : bulkhead, 1: ratelimiter
    private int type;
    //0 : update/create, 1: delete
    private int doing;
    private long id;
    private String name;
    private BulkheadDto bulkhead;
    private RateLimiterDto rateLimiter;
}
