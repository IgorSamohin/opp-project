package simulator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import generator.ship.Ship;
import java.io.File;
import java.io.IOException;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Phaser;

/**
 * Service-3.
 * Takes schedule from Service-2, create delays and early arrivals, do the simulation, return data.
 */
public class Simulator {
    private int amountOfLoaders = 5;
    private int loaderPerformance = 100;
    private Random random = new Random();
    private final int maxDelay = 10_080;
    private final int maxTime = 43_200;
    private List<Thread> loaders = new ArrayList<>();
    private List<Worker> workers = new ArrayList<>();
    private List<Ship> report = new ArrayList<>();
    private ConcurrentLinkedQueue<Ship> arrivedShips = new ConcurrentLinkedQueue<>();
    private int currentTime = 0;
    private volatile boolean canWork = true;

    public List<Ship> getReport() {
        return report;
    }

    private void makeDelays(List<Ship> ships) {
        for (Ship ship : ships) {
            int delay = random.nextInt(maxDelay * 2) - maxDelay;
            if (delay < 0) {
                delay = -Math.min(ship.getArrivalDate(), -delay);
            }
            ship.increaseArrivalDate(delay);
        }
    }

    /**
     * Do preparations before simulation beginning
     */
    public void run() throws IOException, InterruptedException {
        List<Ship> schedule = this.getSchedule();
        List<Ship> actualSchedule = new ArrayList<>(schedule);

        this.makeDelays(actualSchedule);

        actualSchedule.sort(Comparator.comparingInt(Ship::getArrivalDate));

        Phaser phaser = new Phaser(amountOfLoaders + 1);

        this.startThreads(phaser);

        this.startMainCycle(actualSchedule, phaser);

        this.interruptThreads();
    }

    /**
     * Auxiliary method
     *
     * @return id of worker which will be taken by current thread
     */
    private int takeWorker() {
        for (int j = 0; j < amountOfLoaders; j++) {
            if (workers.get(j).takePlace()) {
                return j;
            }
        }
        return -1;
    }

    /**
     * Creates threads with tasks and fill worker's array
     *
     * @param phaser needs for synchronization between loaders and main thread
     */
    private void startThreads(Phaser phaser) {
        for (int i = 0; i < amountOfLoaders; i++) {
            workers.add(new Worker(arrivedShips, loaderPerformance, currentTime));

            Runnable r = () -> threadCycle(phaser);
            loaders.add(new Thread(r));

            loaders.get(i).start();
        }
    }

    /**
     * Declare task for loader
     *
     * @param phaser needs for synchronization between loaders and main thread
     */
    private void threadCycle(Phaser phaser) {
        int takenWorker = -1;

        while (true) {
            phaser.arriveAndAwaitAdvance();
            if(!canWork){
                break;
            }

            //check if taken worker empty or unloaded
            if ((takenWorker >= 0) && (workers.get(takenWorker).isUnloaded() || !workers.get(takenWorker).isBusy())) {
                workers.get(takenWorker).release();
                takenWorker = -1;
            }

            //try to take new worker if thread is not "busy"
            if (takenWorker < 0) {
                takenWorker = takeWorker();
            }

            //do work if thread have taken a worker
            if (takenWorker >= 0) {
                workers.get(takenWorker).work();
            }

            phaser.arriveAndAwaitAdvance();
        }
    }


    /**
     * Main cycle. Manage synchronization, update workers
     *
     * @param actualSchedule - schedule with delays
     * @param phaser         needs for synchronization between loaders and main thread
     */
    private void startMainCycle(List<Ship> actualSchedule, Phaser phaser) {
        canWork = true;
        while (currentTime != maxTime) {
            while (!(actualSchedule.isEmpty()) && (actualSchedule.get(0).getArrivalDate() == currentTime)) {
                arrivedShips.add(actualSchedule.remove(0));
            }

            for (int i = 0; i < amountOfLoaders; i++) {
                Ship ship = workers.get(i).update();
                if (ship != null) {
                    report.add(ship);
                }
            }

            phaser.arriveAndAwaitAdvance();

            ++currentTime;
            phaser.arriveAndAwaitAdvance();
        }
        canWork = false;
        phaser.arriveAndAwaitAdvance();
    }

    private void interruptThreads() {
        for (int i = 0; i < amountOfLoaders; i++) {
            loaders.get(i).interrupt();
        }
    }

    /**
     * In part 2 this method will do GET-request to service-2
     */
    private List<Ship> getSchedule() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, Ship.class);
        return mapper.readValue(new File("src/main/resources/json.json"), collectionType);
    }
}
