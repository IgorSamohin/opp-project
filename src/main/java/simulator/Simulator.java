package simulator;

import generator.ship.Ship;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Phaser;

public class Simulator{
    private int amountOfLoaders;
    private int loaderPerformance;
    private final int maxTime = 43_200;
    private List<Thread> loaders = new ArrayList<>();
    private List<Worker> workers = new ArrayList<>();
    private List<Ship> report = new ArrayList<>();
    private ConcurrentLinkedQueue<Ship> arrivedShips = new ConcurrentLinkedQueue<>();
    private int currentTime = 0;
    private volatile boolean canWork = true;
    private List<Ship> schedule;
    private Random random = new Random();
    private final int maxDelay = 10_080;

    public Simulator(List<Ship> schedule, int amountOfLoaders, int loaderPerformance) {
        this.schedule = new ArrayList<>(schedule);
        this.amountOfLoaders = amountOfLoaders;
        this.loaderPerformance = loaderPerformance;
    }

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
    public void run(){
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
            if (!canWork) {
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
}
