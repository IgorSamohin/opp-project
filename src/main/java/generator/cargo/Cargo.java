package generator.cargo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class describing cargo on a ship
 */
@Data
@NoArgsConstructor
public class Cargo {
    private int params;
    @JsonProperty("cargo_type")
    private CargoType cargoType;

    /**
     * @param cargoType - type of cargo
     * @param params    - weight or amount of containers
     */
    public Cargo(CargoType cargoType, int params) {
        this.cargoType = cargoType;
        this.params = params;
    }
}
