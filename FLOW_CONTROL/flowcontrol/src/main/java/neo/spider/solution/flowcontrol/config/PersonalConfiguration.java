package neo.spider.solution.flowcontrol.config;

public class PersonalConfiguration {

    private int limitForPeriod;
    private long limitRefreshPeriod;
    private long timeoutDuration;

    private static volatile PersonalConfiguration instance;

    private PersonalConfiguration() {}

    public static PersonalConfiguration getInstance() {
        if (instance == null) {
            synchronized (PersonalConfiguration.class) {
                if (instance == null) {
                    instance = new PersonalConfiguration();
                }
            }
        }
        return instance;
    }

    public int getLimitForPeriod() {
        return limitForPeriod;
    }

    public void setLimitForPeriod(int limitForPeriod) {
        this.limitForPeriod = limitForPeriod;
    }

    public long getLimitRefreshPeriod() {
        return limitRefreshPeriod;
    }

    public void setLimitRefreshPeriod(long limitRefreshPeriod) {
        this.limitRefreshPeriod = limitRefreshPeriod;
    }

    public long getTimeoutDuration() {
        return timeoutDuration;
    }

    public void setTimeoutDuration(long timeoutDuration) {
        this.timeoutDuration = timeoutDuration;
    }
}
