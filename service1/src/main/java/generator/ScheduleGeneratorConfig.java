package generator;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "generator")
@Getter
@Setter
public class ScheduleGeneratorConfig {
    private int loaderPerformance;
    private int maxMinutes;
}
