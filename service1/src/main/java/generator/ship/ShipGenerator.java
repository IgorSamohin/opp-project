package generator.ship;

import generator.cargo.Cargo;
import generator.cargo.CargoType;
import generator.cargo.CargosMapFactory;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * Use to generate random ship
 */
public class ShipGenerator {
    private final int maxMinutes;
    private final int maxIntervalBetweenArrival;
    private final int loaderPerformance;
    private final HashSet<String> names = new HashSet<>();
    private final Random random = new Random();
    private final Map<CargoType, Integer> cargosMap = new CargosMapFactory().createCargosMap();

    public ShipGenerator(int loaderPerformance, int maxMinutes, int maxIntervalBetweenArrival) {
        this.loaderPerformance = loaderPerformance;
        this.maxMinutes = maxMinutes;
        this.maxIntervalBetweenArrival = maxIntervalBetweenArrival;
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
        Integer time = cargosMap.get(type);
        time += random.nextInt(maxIntervalBetweenArrival);
        cargosMap.put(type, time);
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
        int n = -1;
        CargoType[] cargoTypes = null;
        do {
            if (n != -1) {
                cargosMap.remove(cargoTypes[n]);
            }

            if (cargosMap.isEmpty()) {
                throw new RuntimeException("All possible ships were generated");
            }

            cargoTypes = cargosMap.keySet().toArray(new CargoType[0]);
            n = random.nextInt(cargoTypes.length);
        } while (cargosMap.get(cargoTypes[n]) >= maxMinutes);

        int amount = random.nextInt(cargoTypes[n].getAmount());
        return new Cargo(amount, cargoTypes[n]);
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
        int unloadingTime = (totalTime > maxMinutes) ? (maxMinutes - arrivalDate) : generatedUnloadingTime;

        Integer time = cargosMap.get(cargo.getCargoType());
        time += unloadingTime;
        cargosMap.put(cargo.getCargoType(), time);

        return unloadingTime;
    }

    public int getCurrentTime() {
        Optional<Integer> min = cargosMap.values().stream().min(Comparator.comparing(Integer::valueOf));
        return min.orElse(maxMinutes);
    }
}
