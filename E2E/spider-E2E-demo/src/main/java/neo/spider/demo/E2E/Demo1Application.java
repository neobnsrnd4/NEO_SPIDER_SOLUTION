package neo.spider.demo.E2E;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class Demo1Application {
	@Autowired
	private ApplicationContext applicationContext;
	public static void main(String[] args) {
		SpringApplication.run(Demo1Application.class, args);
	}
	
	@PostConstruct
    public void checkRestTemplateBean() {
        // RestTemplate 타입의 모든 빈 이름을 가져옴
        String[] beanNames = applicationContext.getBeanNamesForType(RestTemplate.class);
        if (beanNames.length > 0) {
            System.out.println("RestTemplate 빈이 존재합니다: " + Arrays.toString(beanNames));
        } else {
            System.out.println("RestTemplate 빈이 존재하지 않습니다.");
        }
    }

}
