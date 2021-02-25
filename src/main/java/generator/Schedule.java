package generator;

import generator.ship.Ship;
import generator.ship.ShipGenerator;
import java.util.ArrayList;
import java.util.List;

/**
 * Main class of Service-1. Use to get random schedule on 30 days
 */
public class Schedule {
    private final int loaderPerformance;
    private final int amountOfLoaders; //todo сделать учет этого поля в расчетах
    private final static int MAX_MINUTES = 43_200;

    public Schedule(int loaderPerformance, int amountOfLoaders) {
        this.loaderPerformance = loaderPerformance;
        this.amountOfLoaders = amountOfLoaders;
    }

    public List<Ship> getSchedule(){
        List<Ship> schedule = new ArrayList<>();
        ShipGenerator generator = new ShipGenerator(loaderPerformance);
        while (generator.getCurrentTime() < MAX_MINUTES){
            schedule.add(generator.generateShip());
        }
        return schedule;
    }
}
