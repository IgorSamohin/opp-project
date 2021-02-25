package generator;

import java.util.Date;


/**
 * Data class. Describe a ship
 */
public class Ship {
    private final Date arrivalDate;
    private final String name;
    private final Cargo cargo;
    private final int unloadingTime;

    public Ship(Date arrivalDate, String name, Cargo cargo, int unloadingTime) {
        this.arrivalDate = arrivalDate;
        this.name = name;
        this.cargo = cargo;
        this.unloadingTime = unloadingTime;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public String getName() {
        return name;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public int getUnloadingTime() {
        return unloadingTime;
    }
}
