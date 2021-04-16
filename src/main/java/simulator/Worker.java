package simulator;

import generator.ship.Ship;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;

public class Worker {
    @Getter
    private Ship ship = null;
    private final Queue<Ship> arrivedShips;
    private final AtomicInteger amountOfLoaders = new AtomicInteger(0);
    private final int loaderPerformance;
    private final AtomicInteger currentParam = new AtomicInteger(0);
    private int currentTime;
    private int unloadingDelay = 0;

    public Worker(Queue<Ship> arrivedShips, int loaderPerformance, int currentTime) {
        this.arrivedShips = arrivedShips;
        this.loaderPerformance = loaderPerformance;
        this.currentTime = currentTime;
    }

    public boolean isBusy() {
        return ship != null;
    }

    public boolean takePlace() {
        int n = -1;
        int res = -1;
        do {
            n = amountOfLoaders.get();

            if ((isBusy()) && (n < 2)) {
                res = n + 1;
            } else {
                return false;
            }
        } while (amountOfLoaders.compareAndSet(n, res));

        return res != -1;
    }

    public void work()  {
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

    public void release() {
        int n = -1;
        int res = -1;
        do {
            n = amountOfLoaders.get();
            res = n - 1;
        } while (!amountOfLoaders.compareAndSet(n, res));
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
            unloadingDelay = (this.ship != null) ? this.ship.getUnloadingEndDate() : 0;
            currentParam.set((this.ship != null) ? this.ship.getCargo().getParams() : 0);
        }

        ++currentTime;
        return retShip;
    }
}
