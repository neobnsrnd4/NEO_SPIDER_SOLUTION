package neo.spider.solution.apimock.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.github.tomakehurst.wiremock.WireMockServer;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WireMockInitializer implements ApplicationRunner {

    private final WireMockServer wireMockServer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        wireMockServer.start();
        System.out.println("WireMock Server started at: " + wireMockServer.baseUrl());
    }
}
