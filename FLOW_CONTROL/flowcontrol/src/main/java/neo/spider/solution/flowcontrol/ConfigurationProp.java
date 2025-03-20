package neo.spider.solution.flowcontrol;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring")
public class ConfigurationProp {

	private Application application;
	private Data data;
	private Filters filters;

	public Application getApplication() {
		return application;
	}

	public Data getData() {
		return data;
	}

	public Filters getFilters() {
		return filters;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public void setFilters(Filters filters) {
		this.filters = filters;
	}

	public static class Application {
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	public static class Data {
		private Redis redis;

		public Redis getRedis() {
			return redis;
		}

		public void setRedis(Redis redis) {
			this.redis = redis;
		}

		public static class Redis {
			private int port;
			private String host;

			public int getPort() {
				return port;
			}

			public String getHost() {
				return host;
			}

			public void setPort(int port) {
				this.port = port;
			}

			public void setHost(String host) {
				this.host = host;
			}

		}
	}

	public static class Filters {
		private String group1;
		private String group2;

		public String getGroup1() {
			return group1;
		}

		public void setGroup1(String group1) {
			this.group1 = group1;
		}

		public String getGroup2() {
			return group2;
		}

		public void setGroup2(String group2) {
			this.group2 = group2;
		}

	}

}
