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
    private Report report = new Report();


    public void run() throws IOException, InterruptedException {
        List<Ship> commonSchedule = this.getSchedule();

        List<Ship> bulkSchedule = new ArrayList<>();
        List<Ship> liquidSchedule = new ArrayList<>();
        List<Ship> containerSchedule = new ArrayList<>();

        for (Ship s : commonSchedule) {
            switch (s.getCargo().getCargoType()) {
                case BULK -> bulkSchedule.add(s);
                case LIQUID -> liquidSchedule.add(s);
                case CONTAINER -> containerSchedule.add(s);
            }
        }

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


        report.merge(bulkSimulator.getReport(),
                liquidSimulator.getReport(),
                containerSimulator.getReport());
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

    public Report getReport() {
        return report;
    }
}
