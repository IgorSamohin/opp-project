package simulator;

import com.fasterxml.jackson.annotation.JsonProperty;
import generator.ship.Ship;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class Report {
    @JsonProperty("unloading_history")
    List<Ship> unloadingHistory = new ArrayList<>();

    @JsonProperty("ship_amount")
    int shipAmount;

    @JsonProperty("avg_queue_size")
    int avgQueueSize;

    @JsonProperty("avg_waiting_time")
    int avgWaitingTime;

    @JsonProperty("max_delay")
    int maxDelay;

    @JsonProperty("avg_delay")
    int avgDelay;
    int tax;

    @JsonProperty("requestedLoadersAmount")
    int requestedLoadersAmount;

    public void addShip(Ship ship) {
        unloadingHistory.add(ship);
    }

    public void merge(Report... reports) {
        for (Report r : reports) {
            unloadingHistory.addAll(r.getUnloadingHistory());
        }
    }
}
