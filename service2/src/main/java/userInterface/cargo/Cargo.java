package userInterface.cargo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data class describing cargo on a ship
 * cargoType - type of cargo
 * params    - weight or amount of containers
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cargo {
    private int params;
    @JsonProperty("cargo_type")
    private CargoType cargoType;
}
