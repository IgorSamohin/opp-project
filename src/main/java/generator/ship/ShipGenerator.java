package generator.ship;

import generator.cargo.Cargo;
import generator.cargo.CargoType;
import java.util.HashSet;
import java.util.Random;

/**
 * Use to generate random ship
 */
public class ShipGenerator {
    private final int loaderPerformance;
    private final HashSet<String> names = new HashSet<>();
    private Random random = new Random();
    private int currentTime = 0;

    public ShipGenerator(int loaderPerformance) {
        this.loaderPerformance = loaderPerformance;
    }

    public Ship generateShip() {
        int arrivalDate = generateArrivalDate();
        String name = generateName();
        Cargo cargo = generateCargo();
        double unloadingTime = generateUnloadingTime(cargo, loaderPerformance);

        currentTime += unloadingTime;
        return new Ship(name, cargo, arrivalDate, unloadingTime);
    }

    /**
     * @return Date of ship arrival
     */
    private int generateArrivalDate() {
        currentTime += random.nextInt(30);
        return currentTime;
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
     */
    private Cargo generateCargo() {
        CargoType[] values = CargoType.values();
        int n = random.nextInt(values.length);

        int amount = switch (values[n]) {
            case CONTAINER -> random.nextInt(10_000);
            case BULK -> random.nextInt(200_000);
            case LIQUID -> random.nextInt(500_000);
        };

        return new Cargo(values[n], amount);
    }

    /**
     * @param cargo       characteristics of cargo: type and weight/amount of containers
     * @param performance performance of a loader (tons(amount) per hour)
     * @return time of ship unloading
     */
    private double generateUnloadingTime(Cargo cargo, int performance) {
        return ((double) cargo.getParams()) / performance;
    }

    public int getCurrentTime() {
        return currentTime;
    }
}
