package simulator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import simulator.util.Manager;
import simulator.util.Report;

import java.util.List;

@Controller
public class SimulatorUI {
    @Autowired
    private RepositoryImpl repository;

    @GetMapping("/report")
    @ResponseBody
    public List<Report> getReport(@RequestParam(value = "filename") String filename) {
        System.out.println(filename);

        Manager manager = new Manager(repository.getSchedule());
        try {
            manager.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return manager.getReports();
    }


}
