package generator;

import java.util.Date;

/**
 * Use to generate random ship
 */
public class ShipGenerator {
    private final int loaderPerformance;

    public ShipGenerator(int loaderPerformance) {
        this.loaderPerformance = loaderPerformance;
    }

    public Ship generateShip(){
        return (Ship) new Object();
    }

    /**
     * @return Date of ship arrival
     */
    private Date getRandomArrivalDate() {
        return new Date();
    }

    /**
     * @return name of a ship
     */
    private String getRandomName() {
        return "";
    }

    /**
     * @return characteristics of a cargo
     */
    private Cargo getRandomCargo() {
        return new Cargo(CargoType.BULK, 0);
    }

    /**
     * @param cargo       characteristics of cargo: type and weight/amount of containers
     * @param performance performance of a loader
     * @return time of ship unloading
     */
    private int getUnloadingTime(Cargo cargo, int performance) {
        return 0;
    }
}
