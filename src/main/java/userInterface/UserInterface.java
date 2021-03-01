package userInterface;

import generator.ScheduleGenerator;
import generator.ship.Ship;
import java.util.List;

/**
 * Service-2.
 * Gets the schedule from Service-1, checks terminal to additional recordings, runs Service-3 (simulator).
 */
public class UserInterface {
    private final int loaderPerformance;
    private final int amountOfLoaders;

    private final ScheduleGenerator scheduleGenerator;

    public UserInterface(int loaderPerformance, int amountOfLoaders) {
        this.loaderPerformance = loaderPerformance;
        this.amountOfLoaders = amountOfLoaders;
        this.scheduleGenerator = new ScheduleGenerator(loaderPerformance,amountOfLoaders);
    }

    /**
     * In part 2 this method will do GET-request
     */
    private List<Ship> getSchedule(){
        return scheduleGenerator.getSchedule();
    }

    private void writeJson(List<Ship> ships, String fileName){

    }

    private Ship readShip(){
        return (Ship) new Object();
    }

}
