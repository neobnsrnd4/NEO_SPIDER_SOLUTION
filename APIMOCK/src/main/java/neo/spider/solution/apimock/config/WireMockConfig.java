package neo.spider.solution.apimock.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.matching.UrlPattern;

@Configuration
public class WireMockConfig {

	@Bean
	public WireMockServer wireMockServer() {
		WireMockServer server = new WireMockServer(
	            WireMockConfiguration.wireMockConfig()
                .port(56789)
                .usingFilesUnderClasspath("wiremock")
                .withRootDirectory("src/main/resources/wiremock")
                .enableBrowserProxying(true) // proxy 사용 가능
                .proxyPassThrough(true)
                .extensions(new ResponseTransform())
                //.notifier(new ConsoleNotifier(true)) // console 로그
        );
		server.start();

		//파비콘 No Content 처리
		server.stubFor(WireMock.request("GET", new UrlPattern(WireMock.equalTo("/favicon.ico"), false))
			    .willReturn(WireMock.aResponse()
			            .withStatus(204)
			            .withHeader("Content-Type", "text/plain")
			    	));	
		
		//파비콘 계속 생성 방지
		server.removeStub(WireMock.get(WireMock.urlEqualTo("/favicon.ico"))
			    .willReturn(WireMock.aResponse()
			        .withStatus(204)
			        .withHeader("Content-Type", "text/plain")
			    ));
				
		return server;
    }
}
