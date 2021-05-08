package userInterface;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import userInterface.ship.Ship;

@Component
public class RepositoryImpl implements Repository {
    @Autowired
    RestTemplate template;

    @Override
    public List<Ship> getSchedule() {
        String s = template.getForObject("http://localhost:8081/schedule", String.class);

        ObjectMapper mapper = new ObjectMapper();
        CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, Ship.class);
        try {
            return mapper.readValue(s,collectionType); //todo
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getReport(String s) {
        return template.getForObject("http://localhost:8083/report?filename=json.json", String.class);
//        return "THIS IS REPORT!";
    }
}
