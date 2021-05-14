package simulator.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

import lombok.Getter;
import simulator.ship.Ship;

/**
 * Service-3.
 * Takes schedule from Service-2, create delays and early arrivals, do the simulation, return data.
 */
public class Manager {
    private int loaderPerformance = 5000;
    private int amountOfBulkLoaders = 1;
    private int amountOfLiquidLoaders = 1;
    private int amountOfContainersLoaders = 1;
    @Getter
    private Report report = new Report();
    private Random random = new Random();
    private final int MAX_ARRIVE_DELAY = 10_080;
    private final int MAX_UNLOAD_DELAY = 1440;
    private final int MAX_TIME = 43_200;

    private List<Ship> commonSchedule;

    private List<Ship> bulkSchedule = new ArrayList<>();
    private List<Ship> liquidSchedule = new ArrayList<>();
    private List<Ship> containerSchedule = new ArrayList<>();

    private Phaser phaser = new Phaser(3);
    private ExecutorService simulators = Executors.newFixedThreadPool(3);

    public Manager(List<Ship> ships) {
        this.commonSchedule = ships;
    }

    public void run() throws IOException, InterruptedException {
//        commonSchedule = this.getSchedule();
        this.makeDelays(commonSchedule);
        commonSchedule.sort(Comparator.comparingInt(Ship::getActualArrivalDate));

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

        CountDownLatch countDownLatch = new CountDownLatch(3);

        startSimulation(bulkSimulator, amountOfBulkLoaders, countDownLatch);
        startSimulation(liquidSimulator, amountOfLiquidLoaders, countDownLatch);
        startSimulation(containerSimulator, amountOfContainersLoaders, countDownLatch);

        countDownLatch.await();

        Report b = bulkSimulator.getReport();
        Report l = liquidSimulator.getReport();
        Report c = containerSimulator.getReport();

        report.merge(b, l, c);
        System.out.println(report.getFine());

        simulators.shutdown();
    }

    private void startSimulation(Simulator simulator, int amountOfLoaders, CountDownLatch cdl) {
        simulator.setAmountOfLoaders(amountOfLoaders);
        simulators.execute(() -> {
            phaser.arriveAndAwaitAdvance();
            simulator.run();
            cdl.countDown();
        });
    }

    /**
     * In part 2 this method will do GET-request to service-2
     */
    private List<Ship> getSchedule() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, Ship.class);
        return mapper.readValue(new File("src/main/resources/json.json"), collectionType);
    }

    private void makeDelays(List<Ship> ships) {
        for (Ship ship : ships) {
            int arrivalDelay = random.nextInt(MAX_ARRIVE_DELAY * 2) - MAX_ARRIVE_DELAY;
            if (arrivalDelay < 0) {
                arrivalDelay = -Math.min(ship.getPlannedArrivalDate(), -arrivalDelay);
            }

            if (ship.getPlannedArrivalDate() + arrivalDelay + ship.getUnloadingTime() >= MAX_TIME) {
                ship.setActualArrivalDate(MAX_TIME - ship.getPlannedArrivalDate() - ship.getUnloadingTime());
            } else {
                ship.setActualArrivalDate(ship.getPlannedArrivalDate() + arrivalDelay);
            }

            int unloadDelay = random.nextInt(MAX_UNLOAD_DELAY);
            if (ship.getActualArrivalDate() + ship.getUnloadingTime() + unloadDelay >= MAX_TIME) {
                ship.setUnloadingEndDate(MAX_TIME - ship.getActualArrivalDate() - ship.getUnloadingTime());
            } else {
                ship.setUnloadingEndDate(unloadDelay);
            }
        }
    }
}
