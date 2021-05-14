package generator.cargo;

import lombok.Getter;

/**
 * Type of cargo on a ship
 */
public enum CargoType {
    BULK(20_000),
    LIQUID(100_000),
    CONTAINER(300_000);

    @Getter
    private final int amount;

    CargoType(int amount) {
        this.amount = amount;
    }

}
