package simulator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import simulator.ship.Ship;
import simulator.util.Report;

import java.util.List;

@org.springframework.stereotype.Repository
public class RepositoryImpl implements Repository {
    private static final String SERVICE_TWO_SCHEDULE_URL = "http://localhost:8082/schedule?filename=json.json";
    private static final String SERVICE_TWO_RESULT_URL = "http://localhost:8082/results?filename=report.json";
    private final Logger logger = LoggerFactory.getLogger(RepositoryImpl.class);

    @Autowired
    RestTemplate template;

    public List<Ship> getSchedule() {
        String s = template.getForObject(SERVICE_TWO_SCHEDULE_URL, String.class);
        logger.info(s);
        ObjectMapper mapper = new ObjectMapper();
        CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, Ship.class);
        try {
            return mapper.readValue(s, collectionType); //todo
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void sendReport(List<Report> report) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper mapper = new ObjectMapper();
        String s = mapper.writeValueAsString(report);
        HttpEntity<String> httpEntity = new HttpEntity<>(s, headers);
        template.postForObject(SERVICE_TWO_RESULT_URL, httpEntity, String.class);
    }

}
