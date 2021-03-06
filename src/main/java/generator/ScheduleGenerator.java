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
    private final int amountOfLoaders; //todo сделать учет этого поля в расчетах
    private final static int MAX_MINUTES = 43_200; //todo потенциально нужно вести 3 разных времени, отвечающих за разные потоки разгрузки, чтобы не было простоев у кранов

    public ScheduleGenerator(int loaderPerformance, int amountOfLoaders) {
        this.loaderPerformance = loaderPerformance;
        this.amountOfLoaders = amountOfLoaders;
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
            double unloadingTime = ship.getUnloadingTime();
            int days1 = (int) (unloadingTime / 1440.0);
            int hours1 = (int) ((unloadingTime - days1 * 1440.0) / 60.0);
            int minutes1 = (int) (unloadingTime - hours1 * 60 - days1 * 1440);
            String unloadingTime1 = "" + days1 + ":" + hours1 + ":" + minutes1;

            double unloadingTime2 = ship.getArrivalDate();
            int days2 = (int) (unloadingTime2 / 1440.0);
            int hours2 = (int) ((unloadingTime2 - days2 * 1440.0) / 60.0);
            int minutes2 = (int) (unloadingTime2 - hours2 * 60 - days2 * 1440);
            String unloadingTime3 = "" + days2 + ":" + hours2 + ":" + minutes2;

            System.out.printf("Name: %s, Cargo type: %-9s, Cargo parameters: %-6s, Arrival date: %-8s, Planned unloading time: %-8s \n",
                    ship.getName(), ship.getCargo().getCargoType(), ship.getCargo().getParams(), unloadingTime3, unloadingTime1);
        }
    }
}
