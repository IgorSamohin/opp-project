package generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class GeneratorUI {
    @Autowired
    private ScheduleGenerator generator;

    @GetMapping("/schedule")
    @ResponseBody
    public String getSchedule() {
        try {
            return generator.getSerializedSchedule();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return HttpStatus.NOT_ACCEPTABLE.toString();
        }
    }
}
