package generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import generator.ship.Ship;
import generator.ship.ShipGenerator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Service-1.
 * Use to get random schedule on 30 days.
 */
@Repository
public class ScheduleGenerator {
    ScheduleGeneratorConfig config;

    public ScheduleGenerator(@Autowired ScheduleGeneratorConfig config) {
        this.config = config;
    }

    public List<Ship> getSchedule() {
        List<Ship> schedule = new ArrayList<>();
        ShipGenerator generator = new ShipGenerator(config.getLoaderPerformance());
        while (generator.getCurrentTime() < config.getMaxMinutes()) {
            schedule.add(generator.generateShip());
        }
        schedule.sort(Comparator.comparingInt(Ship::getPlannedArrivalDate));
        logSchedule(schedule);
        return schedule;
    }

    public String getSerializedSchedule() throws JsonProcessingException {
        List<Ship> ships = getSchedule();

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(ships);
    }

    private void logSchedule(List<Ship> ships) {
        System.out.println("Current schedule: ");
        for (Ship ship : ships) {
            String unloadingTime = formatDate(ship.getUnloadingTime());
            String arrivalDate = formatDate(ship.getPlannedArrivalDate());

            System.out.printf("Name: %s, Cargo type: %-9s, Cargo parameters: %-6s, Arrival date: %-8s, Planned " +
                            "unloading time: %-8s %n",
                    ship.getName(),
                    ship.getCargo().getCargoType(),
                    ship.getCargo().getParams(),
                    arrivalDate,
                    unloadingTime);
        }
    }

    private String formatDate(double time) {
        int days = (int) (time / 1440.0);
        int hours = (int) ((time - days * 1440.0) / 60.0);
        int minutes = (int) (time - hours * 60 - days * 1440);
        return "" + days + ":" + hours + ":" + minutes;
    }
}
