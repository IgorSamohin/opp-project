package simulator.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import simulator.ship.Ship;

@Getter
@Setter
public class Report {
    private static final int ONE_HOUR_BILL = 100;
    private static final int ONE_LOADER_COST = 30_0000;
    private final int MAX_TIME = 43_200;
    @JsonProperty("unloading_history")
    private List<Ship> unloadingHistory = new ArrayList<>();

    @JsonProperty("ship_amount")
    private int shipAmount = 0;

    @JsonProperty("avg_queue_size")
    private double avgQueueSize = 0;

    @JsonProperty("avg_waiting_time")
    private double avgWaitingTime = 0;

    @JsonProperty("max_delay")
    private int maxDelay = 0;

    @JsonProperty("avg_delay")
    private double avgDelay = 0;
    private int fine = 0;

    @JsonProperty("requestedLoadersAmount")
    private int requestedLoadersAmount;

    public void addShip(Ship ship) {
        unloadingHistory.add(new Ship(ship));
    }

    public void merge(Report... reports) {
        for (Report r : reports) {
            unloadingHistory.addAll(r.getUnloadingHistory());
            fine += r.getFine();
        }
    }

    public void calculateStats() {
        shipAmount = unloadingHistory.size();
        int maxDelayTemp = 0;
        for (Ship ship : unloadingHistory) {
            double delay = ship.getUnloadingEndDate() - ship.getUnloadingStartDate() - ship.getUnloadingTime();
            if (delay > maxDelayTemp) {
                maxDelayTemp = this.maxDelay;
                this.maxDelay = ship.getUnloadingEndDate() - ship.getUnloadingStartDate() - ship.getUnloadingTime();
            }

            avgDelay += (delay > 0) ? delay / shipAmount : 0;
            avgWaitingTime += ((double) ship.getUnloadingStartDate() - (double) ship.getActualArrivalDate()) / shipAmount;
        }
        calculateFine();
    }

    public void sortByArrivalDate() {
        unloadingHistory.sort(Comparator.comparingInt(Ship::getPlannedArrivalDate));
    }


    public int count = 0;
    /**
     * Reset calculated fine and calculate new total fine for report
     *
     * @return calculated fine
     */
    private int calculateFine() {
        fine = 0;
        for (Ship ship : unloadingHistory) {
            int delayInHour = 0;

            if (ship.getUnloadingEndDate() < 0) {
                count++;
                fine += ONE_LOADER_COST;
                continue;
            }

            if (ship.getPlannedArrivalDate() <= ship.getActualArrivalDate()) {
                //arrived with delay
                delayInHour = (ship.getUnloadingEndDate() - ship.getActualArrivalDate() - ship.getUnloadingTime()) / 60;
            } else {
                //arrived in time
                delayInHour =
                        (ship.getUnloadingEndDate() - ship.getPlannedArrivalDate() - ship.getUnloadingTime()) / 60;
            }
            fine += (delayInHour > 0) ? delayInHour * ONE_HOUR_BILL : 0;
        }
        return fine;
    }
}
