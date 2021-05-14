package simulator.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Phaser;

import lombok.Setter;
import simulator.ship.Ship;

public class Simulator {
    @Setter
    private int amountOfLoaders;
    private final int loaderPerformance;
    private final int MAX_TIME = 43_200;
    private final int ONE_LOADER_COAST = 30_000;
    private List<Thread> loaders;
    private List<Worker> workers;
    private Report report;
    private Report oldReport;
    private ConcurrentLinkedQueue<Ship> arrivedShips;
    private int currentTime = 0;
    private volatile boolean canWork = true;
    private final List<Ship> schedule;

    private boolean notAllServed = true;

    public Simulator(List<Ship> schedule, int amountOfLoaders, int loaderPerformance) {
        this.schedule = schedule;
        this.amountOfLoaders = amountOfLoaders;
        this.loaderPerformance = loaderPerformance;
        this.oldReport = new Report();
    }

    /**
     * Do preparations before simulation beginning
     */
    public void run() {
        while (true) {
            currentTime = 0;
            arrivedShips = new ConcurrentLinkedQueue<>();
            loaders = new ArrayList<>();
            workers = new ArrayList<>();
            report = new Report();
            Phaser phaser = new Phaser(amountOfLoaders + 1);

            this.startThreads(phaser);

            this.startMainCycle(new ArrayList<>(schedule), phaser);

            this.interruptThreads();
            report.calculateStats();

            int newFine = report.getFine() + (amountOfLoaders - 1) * ONE_LOADER_COAST;
            int oldFine = oldReport.getFine() + (amountOfLoaders - 2) * ONE_LOADER_COAST;
            System.out.printf("%s   N: %-4s,  count: %-2s   newFine: %-8s,   oldFine %-8s%n",
                    Thread.currentThread().getName(),
                    amountOfLoaders,
                    report.count,
                    newFine,
                    oldFine
            );
            /*TODO: Симуляция проходит очень долго из-за необходимости синхронизировать много потоков.
             *  Потенциальное решение: подбор количества кранов бинарным поиском
             */
            if (!notAllServed && (newFine > oldFine)) {
                if (oldReport.getUnloadingHistory().isEmpty()) {
                    oldReport = report;
                }
                oldReport.setRequestedLoadersAmount(amountOfLoaders);
                break;
            }

            ++amountOfLoaders;
            oldReport = report;
        }
    }

    /**
     * Auxiliary method
     *
     * @return id of worker which will be taken by current thread
     */
    private int takeWorker() {
        for (int j = 0; j < amountOfLoaders / 2 + 1; j++) {
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
            if (i < (amountOfLoaders / 2 + 1)) {
                workers.add(new Worker(arrivedShips, loaderPerformance, currentTime));
            }

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
        while (currentTime != MAX_TIME) {
            while (!(actualSchedule.isEmpty()) && (actualSchedule.get(0).getActualArrivalDate() == currentTime)) {
                arrivedShips.add(new Ship(actualSchedule.remove(0)));
            }

            for (int i = 0; i < amountOfLoaders / 2 + 1; i++) {
                Ship ship = workers.get(i).update();
                if (ship != null) {
                    report.addShip(ship);
                }
            }

            report.setAvgQueueSize(report.getAvgQueueSize() + ((double) arrivedShips.size() / (double) MAX_TIME));

            phaser.arriveAndAwaitAdvance();

            ++currentTime;
            phaser.arriveAndAwaitAdvance();
        }
        canWork = false;
        phaser.arriveAndAwaitAdvance();

        notAllServed = false;
        for (int i = 0; i < amountOfLoaders / 2 + 1; i++) {
            Ship ship = workers.get(i).getShip();
            if ((ship != null) && !workers.get(i).isUnloaded()) {
                ship.setUnloadingEndDate(-1);
                report.addShip(ship);
                notAllServed = true;
            }
        }

        for (Ship ship : arrivedShips) {
            ship.setUnloadingEndDate(-2);
            report.addShip(ship);
            notAllServed = true;
        }

    }

    private void interruptThreads() {
        for (int i = 0; i < amountOfLoaders; i++) {
            loaders.get(i).interrupt();
        }
    }

    public Report getReport() {
        return oldReport;
    }
}
