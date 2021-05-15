package simulator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import simulator.ship.Ship;
import simulator.util.Report;

import java.util.List;

@org.springframework.stereotype.Repository
public class RepositoryImpl implements Repository {
    private static final String SERVICE_TWO_SCHEDULE_URL = "http://localhost:8082/schedule?filename=json.json";
    private static final String SERVICE_TWO_RESULT_URL = "http://localhost:8082/results?filename=json.json";

    @Autowired
    RestTemplate template;

    public List<Ship> getSchedule() {
        String s = template.getForObject(SERVICE_TWO_SCHEDULE_URL, String.class);
        System.out.println(s);
        ObjectMapper mapper = new ObjectMapper();
        CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, Ship.class);
        try {
            return mapper.readValue(s, collectionType); //todo
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void sendReport(Report report) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

//        template.postForObject(SERVICE_TWO_RESULT_URL, report, );
    }

}
