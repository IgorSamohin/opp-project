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
    @JsonProperty("arrival_date")
    private final Date arrivalDate;
    private final String name;
    private final Cargo cargo;
    @JsonProperty("unloading_time")
    private final int unloadingTime;

    public Ship(Date arrivalDate, String name, Cargo cargo, int unloadingTime) {
        this.arrivalDate = arrivalDate;
        this.name = name;
        this.cargo = cargo;
        this.unloadingTime = unloadingTime;
    }
}
