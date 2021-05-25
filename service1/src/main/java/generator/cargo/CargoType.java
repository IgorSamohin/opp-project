package generator.cargo;

import lombok.Getter;

/**
 * Type of cargo on a ship
 */
public enum CargoType {
    BULK(10_000),
    LIQUID(15_000),
    CONTAINER(20_000);

    @Getter
    private final int amount;

    CargoType(int amount) {
        this.amount = amount;
    }
}
