package simulator.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

import lombok.Getter;
import simulator.cargo.CargoType;
import simulator.ship.Ship;

/**
 * Service-3.
 * Takes schedule from Service-2, create delays and early arrivals, do the simulation, return data.
 */
public class Manager {
    private ManagerConfig config = new ManagerConfig();
    @Getter
    private final List<Report> reports = new ArrayList<>();
    private final Random random = new Random();
    private final List<Ship> commonSchedule;
    private final Phaser phaser;
    private final ExecutorService simulators;

    public Manager(List<Ship> ships) {
        this.commonSchedule = ships;
        phaser = new Phaser(config.getTypesAmount());
        simulators  = Executors.newFixedThreadPool(config.getTypesAmount());
    }

    public void run() throws InterruptedException {
        this.makeDelays(commonSchedule);
        commonSchedule.sort(Comparator.comparingInt(Ship::getActualArrivalDate));
        Map<CargoType, Simulator> simulatorsMap = new SimulatorsFactory().createSimulators(commonSchedule,
                1, config.getLoaderPerformance());

        CountDownLatch countDownLatch = new CountDownLatch(3);

        for (Simulator s : simulatorsMap.values()) {
            startSimulation(s, countDownLatch);
        }

        countDownLatch.await();

        for (Simulator s : simulatorsMap.values()) {
            reports.add(s.getReport());
        }

        this.simulators.shutdown();
    }

    private void startSimulation(Simulator simulator, CountDownLatch cdl) {
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
            int arrivalDelay = random.nextInt(config.getMaxArriveDelay() * 2) - config.getMaxArriveDelay();
            if (arrivalDelay < 0) {
                arrivalDelay = -Math.min(ship.getPlannedArrivalDate(), -arrivalDelay);
            }

            if (ship.getPlannedArrivalDate() + arrivalDelay + ship.getUnloadingTime() >= config.getMaxTime()) {
                ship.setActualArrivalDate(config.getMaxTime() - ship.getPlannedArrivalDate() - ship.getUnloadingTime());
            } else {
                ship.setActualArrivalDate(ship.getPlannedArrivalDate() + arrivalDelay);
            }

            int unloadDelay = random.nextInt(config.getMaxUnloadDelay());
            if (ship.getActualArrivalDate() + ship.getUnloadingTime() + unloadDelay >= config.getMaxTime()) {
                ship.setUnloadingEndDate(config.getMaxTime() - ship.getActualArrivalDate() - ship.getUnloadingTime());
            } else {
                ship.setUnloadingEndDate(unloadDelay);
            }
        }
    }
}
