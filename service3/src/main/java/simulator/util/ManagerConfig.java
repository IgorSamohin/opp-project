package simulator.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(value = "manager")
public class ManagerConfig {
    private int loaderPerformance = 5000;
    private int amountOfBulkLoaders = 1;
    private int amountOfLiquidLoaders = 1;
    private int amountOfContainersLoaders = 1;

    private int maxArriveDelay = 10_080;
    private int maxUnloadDelay = 1440;
    private int maxTime = 43_200;

    private int typesAmount = 3;
}
