package simulator.util;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;
import simulator.ship.Ship;

public class Worker {
    @Getter
    private Ship ship = null;
    private final ConcurrentLinkedQueue<Ship> arrivedShips;
    private volatile int amountOfLoaders = 0;
    private final int loaderPerformance;
    private final AtomicInteger currentParam = new AtomicInteger(0);
    private int currentTime;
    private int unloadingDelay = 0;

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
        return (currentParam.get() <= 0) && (unloadingDelay <= 0);
    }

    public synchronized void release() {
        --amountOfLoaders;
    }

    public Ship update() {
        Ship retShip = null;
        if ((currentParam.get() <= 0) && (unloadingDelay > 0)) {
            --unloadingDelay;
        }

        if (this.isBusy() && this.isUnloaded()) {
            ship.setUnloadingEndDate(currentTime);
            retShip = this.ship;
            this.ship = null;
        }

        if (!this.isBusy()) {
            this.ship = arrivedShips.poll();
            if (ship != null) {
                this.ship.setUnloadingStartDate(currentTime);
            }
            unloadingDelay = (this.ship != null) ? this.ship.getUnloadingEndDate() : 0;
            currentParam.set((this.ship != null) ? this.ship.getCargo().getParams() : 0);
        }

        ++currentTime;
        return retShip;
    }
}
