package neo.spider.solution.flowcontrol.filter.ratelimiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import neo.spider.solution.flowcontrol.ConfigurationProp;
import neo.spider.solution.flowcontrol.service.RedisService;

@Component
public class FilterManager {

	private final RedisService redisService;
	private final ConfigurationProp prop;
	private final Map<String, Boolean> filterGroups = new ConcurrentHashMap<String, Boolean>();

	public FilterManager(RedisService redisService, ConfigurationProp prop) {
		this.redisService = redisService;
		this.prop = prop;
		setFilterGroup();
		System.out.println("filterGroups : " + filterGroups.toString());

	}

	// 필터 그룹 초기 세팅
	public void setFilterGroup() {
		String appName = prop.getApplication().getName();
		String group1 = prop.getFilters().getGroup1();
		String group2 = prop.getFilters().getGroup2();
		String resilience4jKey = appName + "/filterManager/" + group1;
		String bucket4jKey = appName + "/filterManager/" + group2;
		
		
		System.out.println("resil result : " + redisService.getStringValue(resilience4jKey));
		
		Boolean resilience4jVal = (redisService.getStringValue(resilience4jKey) == null) ? true
				: (redisService.getStringValue(resilience4jKey).trim().equals("true") ? true : false);

		Boolean bucket4Val = (redisService.getStringValue(bucket4jKey).trim() == null) ? false
				: (redisService.getStringValue(bucket4jKey).equals("true") ? true : false);
		filterGroups.put(group1, resilience4jVal);
		filterGroups.put(group2, bucket4Val);

	}

	public boolean isGroupEnabled(String groupName) {
		return filterGroups.getOrDefault(groupName, false);
	}

	public void enableGroup(String groupName) {
		filterGroups.put(groupName, true);
		System.out.println(groupName + " filter is enabled.");
	}

	public void disableGroup(String groupName) {
		filterGroups.put(groupName, false);
		System.out.println(groupName + " filter is disabled.");
	}

}
