package neo.spider.solution.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpiderBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpiderBatchApplication.class, args);
	}

}
