package generator.ship;

import generator.cargo.Cargo;
import generator.cargo.CargoType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Use to generate random ship
 */
public class ShipGenerator {
    private static final int MAX_MINUTES = 43_200;
    private final int loaderPerformance;
    private final HashSet<String> names = new HashSet<>();
    private Random random = new Random();
    private int currentBulkTime = 0;
    private int currentLiquidTime = 0;
    private int currentContainerTime = 0;

    public ShipGenerator(int loaderPerformance) {
        this.loaderPerformance = loaderPerformance;
    }

    public Ship generateShip() throws RuntimeException {
        String name = generateName();
        Cargo cargo = generateCargo();
        int arrivalDate = generateArrivalDate(cargo.getCargoType());
        int unloadingTime = generateUnloadingTime(cargo, loaderPerformance, arrivalDate);
        return new Ship(name, cargo, arrivalDate, unloadingTime);
    }

    /**
     * @return Date of ship arrival
     */
    private int generateArrivalDate(CargoType type) {
        int time = 0;
        switch (type) {
            case BULK -> {
                currentBulkTime += random.nextInt(30);
                time = currentBulkTime;
            }
            case LIQUID -> {
                currentLiquidTime += random.nextInt(30);
                time = currentLiquidTime;
            }
            case CONTAINER -> {
                currentContainerTime += random.nextInt(30);
                time = currentContainerTime;
            }
        }
        return time;
    }

    /**
     * @return name of a ship
     */
    private String generateName() {
        int alphabetLength = 26;

        String name = "";
        StringBuilder nameBuilder;
        do {
            nameBuilder = new StringBuilder();
            for (int i = 0; i < 10; i++) {
                nameBuilder.append((char) (random.nextInt(alphabetLength) + 'a'));
            }
            name = nameBuilder.toString();
        } while (names.contains(name));
        names.add(name);
        return name;
    }

    /**
     * @return characteristics of a cargo
     * @throws RuntimeException if all three times ended
     */
    private Cargo generateCargo() throws RuntimeException {
        List<CargoType> list = new ArrayList<>();

        if (currentBulkTime < MAX_MINUTES) {
            list.add(CargoType.BULK);
        }

        if (currentLiquidTime < MAX_MINUTES) {
            list.add(CargoType.LIQUID);
        }

        if (currentContainerTime < MAX_MINUTES) {
            list.add(CargoType.CONTAINER);
        }

        if (list.isEmpty()) {
            throw new RuntimeException("All possible ships were generated");
        }

        int n = random.nextInt(list.size());

        int amount = switch (list.get(n)) {
            case CONTAINER -> random.nextInt(100_000);
            case BULK -> random.nextInt(200_000);
            case LIQUID -> random.nextInt(500_000);
        };

        return new Cargo(list.get(n), amount);
    }

    /**
     * @param cargo       characteristics of cargo: type and weight/amount of containers
     * @param performance performance of a loader (tons(amount) per hour)
     * @param arrivalDate date of ship arrival
     * @return time of ship unloading
     */
    private int generateUnloadingTime(Cargo cargo, int performance, int arrivalDate) {
        int generatedUnloadingTime = cargo.getParams() / performance + 1;
        int totalTime = arrivalDate + generatedUnloadingTime;
        int unloadingTime = (totalTime > MAX_MINUTES) ? (MAX_MINUTES - arrivalDate) : generatedUnloadingTime;

        switch (cargo.getCargoType()) {
            case BULK -> currentBulkTime += unloadingTime;
            case LIQUID -> currentLiquidTime += unloadingTime;
            case CONTAINER -> currentContainerTime += unloadingTime;
        }

        return unloadingTime;
    }

    public int getCurrentTime() {
        return Math.min(currentBulkTime, Math.min(currentLiquidTime, currentContainerTime));
    }
}
