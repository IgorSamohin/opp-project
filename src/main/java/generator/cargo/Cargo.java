package generator.cargo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Data class describing cargo on a ship
 */
@Data
public class Cargo {
    @JsonProperty("cargo_type")
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
}
