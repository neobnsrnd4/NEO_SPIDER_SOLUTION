package neo.spider.solution.flowcontrol;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring")
public class ConfigurationProp {

	private Application application;
	private Data data;

	public Application getApplication() {
		return application;
	}

	public Data getData() {
		return data;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public void setData(Data data) {
		this.data = data;
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

}
