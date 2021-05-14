package generator;

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
    private long millisInMinute;
    private int maxIntervalBetweenArrival;
}
