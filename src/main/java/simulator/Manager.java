package simulator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import generator.cargo.CargoType;
import generator.ship.Ship;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Service-3.
 * Takes schedule from Service-2, create delays and early arrivals, do the simulation, return data.
 */
public class Manager {
    private int loaderPerformance = 100;
    private int amountOfBulkLoaders = 5;
    private int amountOfLiquidLoaders = 5;
    private int amountOfContainersLoaders = 5;
    private List<Ship> report = new ArrayList<>();


    public void run() throws IOException, InterruptedException {
        List<Ship> commonSchedule = this.getSchedule();

        List<Ship> bulkSchedule = commonSchedule.stream()
                .filter(e -> e.getCargo()
                        .getCargoType()
                        .equals(CargoType.BULK))
                .collect(Collectors.toList());
        List<Ship> liquidSchedule = commonSchedule.stream()
                .filter(e -> e.getCargo()
                        .getCargoType()
                        .equals(CargoType.LIQUID))
                .collect(Collectors.toList());
        List<Ship> containerSchedule = commonSchedule.stream()
                .filter(e -> e.getCargo()
                        .getCargoType()
                        .equals(CargoType.CONTAINER))
                .collect(Collectors.toList());

        Simulator bulkSimulator = new Simulator(bulkSchedule, amountOfBulkLoaders, loaderPerformance);
        Simulator liquidSimulator = new Simulator(liquidSchedule, amountOfLiquidLoaders, loaderPerformance);
        Simulator containerSimulator = new Simulator(containerSchedule, amountOfContainersLoaders, loaderPerformance);

        ExecutorService simulators = Executors.newFixedThreadPool(3);

        CountDownLatch countDownLatch = new CountDownLatch(3);

        simulators.execute(() -> {
            bulkSimulator.run();
            countDownLatch.countDown();
        });
        simulators.execute(() -> {
            liquidSimulator.run();
            countDownLatch.countDown();
        });
        simulators.execute(() -> {
            containerSimulator.run();
            countDownLatch.countDown();
        });

        countDownLatch.await();
        report.addAll(bulkSimulator.getReport());
        report.addAll(liquidSimulator.getReport());
        report.addAll(containerSimulator.getReport());
        simulators.shutdown();
    }

    /**
     * In part 2 this method will do GET-request to service-2
     */
    private List<Ship> getSchedule() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, Ship.class);
        return mapper.readValue(new File("src/main/resources/json.json"), collectionType);
    }

    public List<Ship> getReport() {
        return report;
    }
}
