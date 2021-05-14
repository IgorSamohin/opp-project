package userInterface;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import userInterface.ship.Ship;

import java.util.List;

@Component
public class RepositoryImpl implements Repository {
    @Autowired
    RestTemplate template;

    private static final String SERVICE_ONE_URL = "http://localhost:8081/schedule";
    private static final String SERVICE_THREE_URL = "http://localhost:8083/report?filename=json.json";

    @Override
    public List<Ship> getSchedule() {
        String s = template.getForObject(SERVICE_ONE_URL, String.class);

        ObjectMapper mapper = new ObjectMapper();
        CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, Ship.class);
        try {
            return mapper.readValue(s, collectionType); //todo
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getReport(String s) {
        return template.getForObject(SERVICE_THREE_URL, String.class);
//        return "THIS IS REPORT!";
    }
}
