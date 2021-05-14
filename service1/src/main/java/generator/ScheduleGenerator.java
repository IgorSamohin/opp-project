package generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import generator.ship.Ship;
import generator.ship.ShipGenerator;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Service-1.
 * Use to get random schedule on 30 days.
 */
@Repository
public class ScheduleGenerator {
    ScheduleGeneratorConfig config;
    private final Logger logger = LoggerFactory.getLogger(ScheduleGenerator.class);

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
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Current schedule: \n");
        for (Ship ship : ships) {
            String unloadingTime = formatDate(ship.getUnloadingTime());
            String arrivalDate = formatDate(ship.getPlannedArrivalDate());

            stringBuilder.append(String.format("Name: %s, Cargo type: %-9s, Cargo parameters: %-6s, Arrival date: " +
                            "%-8s, Planned unloading time: %-8s %n",
                    ship.getName(),
                    ship.getCargo().getCargoType(),
                    ship.getCargo().getParams(),
                    arrivalDate,
                    unloadingTime));
        }
        logger.info("{}", stringBuilder);
    }

    private String formatDate(double time) {
        Calendar cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone(ZoneId.of("UTC")));
        cal.setTimeInMillis((long) time * config.getMillisInMinute());

        int days = cal.get(Calendar.DAY_OF_MONTH) - 1;
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);

        return "" + days + ":" + hours + ":" + minutes;
    }
}
