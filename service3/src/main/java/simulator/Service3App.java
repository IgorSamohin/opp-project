package simulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class Service3App implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(Service3App.class, args);
    }
}
