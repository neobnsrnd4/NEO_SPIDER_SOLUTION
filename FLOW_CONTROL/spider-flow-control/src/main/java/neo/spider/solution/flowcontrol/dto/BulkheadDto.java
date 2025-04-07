package neo.spider.solution.flowcontrol.dto;

public class BulkheadDto {

    private long id;
    private int application_id;
    private String url;
    private int maxConcurrentCalls;
    private int maxWaitDuration;

    public int getMaxWaitDuration() {
        return maxWaitDuration;
    }

    public void setMaxWaitDuration(int maxWaitDuration) {
        this.maxWaitDuration = maxWaitDuration;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getApplication_id() {
        return application_id;
    }

    public void setApplication_id(int application_id) {
        this.application_id = application_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getMaxConcurrentCalls() {
        return maxConcurrentCalls;
    }

    public void setMaxConcurrentCalls(int maxConcurrentCalls) {
        this.maxConcurrentCalls = maxConcurrentCalls;
    }

    @Override
    public String toString() {
        return "BulkheadConfigDto{" +
                "id=" + id +
                ", application_id=" + application_id +
                ", url='" + url + '\'' +
                ", maxConcurrentCalls=" + maxConcurrentCalls +
                ", maxWaitDuration=" + maxWaitDuration +
                '}';
    }
}
