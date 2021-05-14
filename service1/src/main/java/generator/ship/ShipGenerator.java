package generator.ship;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import generator.cargo.Cargo;
import generator.cargo.CargoType;
import generator.cargo.CargosMapFactory;

/**
 * Use to generate random ship
 */
public class ShipGenerator {
    private static final int MAX_MINUTES = 43_200;
    private final int loaderPerformance;
    private final HashSet<String> names = new HashSet<>();
    private final Random random = new Random();
    private final Map<CargoType, Integer> cargosMap = new CargosMapFactory().createCargosMap();

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
        Integer time = cargosMap.get(type);
        time += random.nextInt(30);
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
        } while (cargosMap.get(cargoTypes[n]) >= MAX_MINUTES);

        int amount = random.nextInt(cargoTypes[n].getAmount());
        return new Cargo(cargoTypes[n], amount);
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

        Integer time = cargosMap.get(cargo.getCargoType());
        time += unloadingTime;
        cargosMap.put(cargo.getCargoType(), time);

        return unloadingTime;
    }

    public int getCurrentTime() {
        Optional<Integer> min = cargosMap.values().stream().min(Comparator.comparing(Integer::valueOf));
        return min.orElse(MAX_MINUTES);
    }
}
