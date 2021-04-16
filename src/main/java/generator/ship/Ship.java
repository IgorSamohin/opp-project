package generator.ship;

import com.fasterxml.jackson.annotation.JsonProperty;
import generator.cargo.Cargo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class. Describe a ship
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ship {
    private String name;
    private Cargo cargo;
    /**
     * Date in minutes
     * maxValue = 43_200
     */
    @JsonProperty("planned_arrival_date")
    private int plannedArrivalDate;
    @JsonProperty("actual_arrival_date")
    private int actualArrivalDate;
    @JsonProperty("unloading_time")
    private int unloadingTime;
    @JsonProperty("unloading_end_date")
    private int unloadingEndDate = 0;

    public Ship(String name, Cargo cargo, int arrivalDate, int unloadingTime) {
        this.plannedArrivalDate = arrivalDate;
        this.name = name;
        this.cargo = cargo;
        this.unloadingTime = unloadingTime;
    }

    public void increaseArrivalDate(int time) {
        plannedArrivalDate += time;
    }
}
