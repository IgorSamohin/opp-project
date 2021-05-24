package userInterface;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import userInterface.ship.Ship;

import java.util.List;

@Getter
@Setter
public class Report {
    @JsonProperty("unloading_history")
    private List<Ship> unloadingHistory;

    @JsonProperty("ship_amount")
    private int shipAmount;

    @JsonProperty("avg_queue_size")
    private double avgQueueSize;

    @JsonProperty("avg_waiting_time")
    private double avgWaitingTime;

    @JsonProperty("max_delay")
    private int maxDelay;

    @JsonProperty("avg_delay")
    private double avgDelay;
    private int fine;

    @JsonProperty("requestedLoadersAmount")
    private int requestedLoadersAmount;
}
