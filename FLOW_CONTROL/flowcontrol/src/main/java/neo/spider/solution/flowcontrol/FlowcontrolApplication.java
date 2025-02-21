package neo.spider.solution.flowcontrol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
@ServletComponentScan
public class FlowcontrolApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlowcontrolApplication.class, args);
    }

}
