package generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class Service1App implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(Service1App.class, args);
    }
}
