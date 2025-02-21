package neo.spider.solution.flowcontrol.dto;

public class UpdateConfigDto {
    //0 : bulkhead, 1: ratelimiter
    private int type;
    //0 : update/create, 1: delete
    private int doing;
    private String name;
    private BulkheadDto bulkhead;
    private RateLimiterDto rateLimiter;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDoing() {
        return doing;
    }

    public void setDoing(int doing) {
        this.doing = doing;
    }

    public BulkheadDto getBulkhead() {
        return bulkhead;
    }

    public void setBulkhead(BulkheadDto bulkhead) {
        this.bulkhead = bulkhead;
    }

    public RateLimiterDto getRateLimiter() {
        return rateLimiter;
    }

    public void setRateLimiter(RateLimiterDto rateLimiter) {
        this.rateLimiter = rateLimiter;
    }
}
