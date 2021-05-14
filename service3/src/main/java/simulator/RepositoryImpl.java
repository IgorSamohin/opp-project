package simulator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import simulator.ship.Ship;

import java.util.List;

@org.springframework.stereotype.Repository
public class RepositoryImpl implements Repository {
    private static final String SERVICE_TWO_URL = "http://localhost:8082/schedule?filename=json.json";

    @Autowired
    RestTemplate template;

    public List<Ship> getSchedule() {
        String s = template.getForObject(SERVICE_TWO_URL, String.class);

        ObjectMapper mapper = new ObjectMapper();
        CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, Ship.class);
        try {
            return mapper.readValue(s, collectionType); //todo
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
