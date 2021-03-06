package simulator;

import generator.ship.Ship;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Worker {
    private Ship ship = null;
    private final ConcurrentLinkedQueue<Ship> arrivedShips;
    private volatile int amountOfLoaders = 0;
    private final int loaderPerformance;
    private final AtomicInteger currentParam = new AtomicInteger(0);
    private int currentTime;

    public Worker(ConcurrentLinkedQueue<Ship> arrivedShips, int loaderPerformance, int currentTime) {
        this.arrivedShips = arrivedShips;
        this.loaderPerformance = loaderPerformance;
        this.currentTime = currentTime;
    }

    public boolean isBusy() {
        return ship != null;
    }

    public synchronized boolean takePlace() {
        if ((isBusy()) && (amountOfLoaders < 2)) {
            ++amountOfLoaders;
            return true;
        }

        return false;
    }

    public void work() {
        int param = -1;
        int res = -1;
        do {
            param = currentParam.get();
            res = param - loaderPerformance;
        } while (!currentParam.compareAndSet(param, res));
    }

    public boolean isUnloaded() {
        return currentParam.get() < 0;
    }

    public synchronized void release() {
        --amountOfLoaders;
    }

    public Ship update() {
        Ship retShip = null;
        if (this.isBusy() && this.isUnloaded()) {
            ship.setUnloadingEndDate(currentTime);
            retShip = this.ship;
            this.ship = null;
        }

        if (!this.isBusy()) {
            this.ship = arrivedShips.poll();
            currentParam.set(this.ship != null ? this.ship.getCargo().getParams() : 0);
        }

        ++currentTime;
        return retShip;
    }
}
