package generator;


/**
 * Data class describing cargo on a ship
 */
public class Cargo {
    private final CargoType cargoType;
    private final int params;

    /**
     * @param cargoType - type of cargo
     * @param params - weight or amount of containers
     */
    public Cargo(CargoType cargoType, int params) {
        this.cargoType = cargoType;
        this.params = params;
    }

    public CargoType getCargoType() {
        return cargoType;
    }

    public int getParams() {
        return params;
    }
}
