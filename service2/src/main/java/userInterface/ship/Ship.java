package userInterface.ship;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import userInterface.cargo.Cargo;

/**
 * Data class. Describe a ship
 */
@Getter
@Setter
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
    @JsonProperty("unloading_start_date")
    private int unloadingStartDate = 0;
    @JsonProperty("unloading_end_date")
    private int unloadingEndDate = 0;

    public Ship(String name, Cargo cargo, int arrivalDate, int unloadingTime) {
        this.plannedArrivalDate = arrivalDate;
        this.name = name;
        this.cargo = cargo;
        this.unloadingTime = unloadingTime;
    }

    public Ship(Ship ship) {
        this.name = ship.getName();
        this.cargo = new Cargo(ship.getCargo().getCargoType(), ship.getCargo().getParams());
        this.plannedArrivalDate = ship.getPlannedArrivalDate();
        this.actualArrivalDate = ship.getActualArrivalDate();
        this.unloadingTime = ship.getUnloadingTime();
        this.unloadingStartDate = ship.getUnloadingStartDate();
        this.unloadingEndDate = ship.getUnloadingEndDate();
    }

    public void increaseArrivalDate(int time) {
        plannedArrivalDate += time;
    }
}
