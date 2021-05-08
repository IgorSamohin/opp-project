package simulator;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import simulator.ship.Ship;
import simulator.util.Manager;
import simulator.util.Report;

@Controller
public class SimulatorUI {
    @Autowired
    private RepositoryImpl repository;

    @GetMapping("/report")
    @ResponseBody
    public Report getReport(@RequestParam(value = "filename") String filename){
        System.out.println(filename);

        Manager manager = new Manager(repository.getSchedule());
        try {
            manager.run();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return manager.getReport();
    }


}
