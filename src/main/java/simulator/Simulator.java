package simulator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import generator.ship.Ship;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;


/**
 * Service-3.
 * Takes schedule from Service-2, create delays and early arrivals, do the simulation, return data.
 */
public class Simulator {
    Random random = new Random();
    private final int maxDelay = 10_080;

    private void makeDelays(List<Ship> ships) {
        for (Ship ship: ships) {
            int delay = random.nextInt(maxDelay * 2) - maxDelay;
            ship.increaseArrivalDate(delay);
        }
    }

    public void run() {

    }

    /**
     * In part 2 this method will do GET-request to service-2
     */
    private List<Ship> getSchedule() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, Ship.class);
        return mapper.readValue(new File("src/main/resources/json.json"), collectionType);
    }
}
