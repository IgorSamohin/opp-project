package generator.ship;

import com.fasterxml.jackson.annotation.JsonProperty;
import generator.cargo.Cargo;
import java.util.Date;
import lombok.Data;


/**
 * Data class. Describe a ship
 */
@Data
public class Ship {
    /** Date in minutes
     * maxValue = 43_200
     */
    @JsonProperty("arrival_date")
    private final int arrivalDate;
    private final String name;
    private final Cargo cargo;
    @JsonProperty("unloading_time")
    private final double unloadingTime;

    public Ship(int arrivalDate, String name, Cargo cargo, double unloadingTime) {
        this.arrivalDate = arrivalDate;
        this.name = name;
        this.cargo = cargo;
        this.unloadingTime = unloadingTime;
    }
}
