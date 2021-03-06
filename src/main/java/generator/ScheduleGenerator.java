package generator;

import generator.ship.Ship;
import generator.ship.ShipGenerator;
import java.util.ArrayList;
import java.util.List;

/**
 * Service-1.
 * Use to get random schedule on 30 days.
 */
public class ScheduleGenerator {
    private final int loaderPerformance;
    private final static int MAX_MINUTES = 43_200; //todo потенциально нужно вести 3 разных времени, отвечающих за разные потоки разгрузки, чтобы не было простоев у кранов

    public ScheduleGenerator(int loaderPerformance) {
        this.loaderPerformance = loaderPerformance;
    }

    public List<Ship> getSchedule() {
        List<Ship> schedule = new ArrayList<>();
        ShipGenerator generator = new ShipGenerator(loaderPerformance);
        while (generator.getCurrentTime() < MAX_MINUTES) {
            schedule.add(generator.generateShip());
        }
        writeSchedule(schedule);
        return schedule;
    }

    private void writeSchedule(List<Ship> ships) {//todo сделать из этого нормальную табличку с нормальным формированием времени
        System.out.println("Current schedule: ");
        for (Ship ship : ships) {
            String unloadingTime = formatDate(ship.getUnloadingTime());
            String arrivalDate = formatDate(ship.getArrivalDate());

            System.out.printf("Name: %s, Cargo type: %-9s, Cargo parameters: %-6s, Arrival date: %-8s, Planned unloading time: %-8s \n",
                    ship.getName(), ship.getCargo().getCargoType(), ship.getCargo().getParams(), arrivalDate, unloadingTime);
        }
    }

    private String formatDate(double time) {
        int days = (int) (time / 1440.0);
        int hours = (int) ((time - days * 1440.0) / 60.0);
        int minutes = (int) (time - hours * 60 - days * 1440);
        return  "" + days + ":" + hours + ":" + minutes;
    }
}
