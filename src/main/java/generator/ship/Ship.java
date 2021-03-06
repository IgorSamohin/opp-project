package generator.ship;

import com.fasterxml.jackson.annotation.JsonProperty;
import generator.cargo.Cargo;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class. Describe a ship
 */
@Data
@NoArgsConstructor
public class Ship {
    private String name;
    private Cargo cargo;
    /**
     * Date in minutes
     * maxValue = 43_200
     */
    @JsonProperty("arrival_date")
    private int arrivalDate;
    @JsonProperty("unloading_time")
    private double unloadingTime;
    private int unloadingEndDate = 0;

    public Ship(String name, Cargo cargo, int arrivalDate, double unloadingTime) {
        this.arrivalDate = arrivalDate;
        this.name = name;
        this.cargo = cargo;
        this.unloadingTime = unloadingTime;
    }

    public void increaseArrivalDate(int time) {
        arrivalDate += time;
    }
}
