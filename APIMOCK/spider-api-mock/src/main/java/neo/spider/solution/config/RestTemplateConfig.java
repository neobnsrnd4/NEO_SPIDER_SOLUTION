package neo.spider.solution.config;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
	
	@Bean
	public RestTemplate restTemplate() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        
        connectionManager.setMaxTotal(50);//최대 연결 수
        connectionManager.setDefaultMaxPerRoute(10);//라우트당 최대 연결 수
        connectionManager.setDefaultSocketConfig(
            SocketConfig.custom()
                .setSoTimeout(Timeout.ofSeconds(5))//소켓 읽기 타임아웃
                .build()
        );

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        factory.setConnectionRequestTimeout(3000);//연결 요청 타임아웃
        return new RestTemplate(factory);
	}
	
}
