package userInterface.cargo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data class describing cargo on a ship
 */
@Getter
@Setter
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
