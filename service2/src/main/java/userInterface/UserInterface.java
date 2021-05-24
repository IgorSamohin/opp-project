package userInterface;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import userInterface.cargo.Cargo;
import userInterface.cargo.CargoType;
import userInterface.ship.Ship;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

/**
 * Service-2.
 * Gets the schedule from Service-1, checks terminal to additional recordings, runs Service-3 (simulator).
 */
@Controller
public class UserInterface {
    @Autowired
    RepositoryImpl repository;

    /**
     * Entry point
     */
    @GetMapping("/start")
    public String startWork() throws IOException {
        String fileName = repository.getSchedule();
        repository.notify(fileName);
        return "redirect:/reports";
    }

    /**
     * Endpoint to receive results from service 3
     *
     * @param report - results
     */
    @PostMapping("/results")
    @ResponseBody
    public String getResults(@RequestBody String report,
                             @RequestParam(name = "filename") String filename) throws IOException {
        repository.saveReport(report);
        System.out.println(report);
        return "ok";//todo
    }

    /**
     * Shows report stored in standard file
     * @return
     */
    @GetMapping("/reports")
    @ResponseBody
    public String showResults() throws IOException {
        List<Report> reports = repository.getReport();
        StringBuilder stringBuilder = new StringBuilder();
//        for (Report r : reports) {
//            r.setUnloadingHistory(null);
//        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(reports);
    }

    /**
     * Endpoint to return data to service 3
     *
     * @param filename - name of the file with data
     */
    @GetMapping("/schedule")
    @ResponseBody
    public String getData(@RequestParam(value = "filename") String filename) throws IOException {
        if (!repository.hasFile(filename)) {
            return "error";//todo
        }

        return repository.readFromFile(filename);
    }

    /**
     * @return a ship read from terminal or error
     */
    private Ship readShip() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Name (String): ");
        String name = reader.readLine();

        System.out.print("Cargo params (int): ");
        int params = Integer.parseInt(reader.readLine());

        System.out.print("Cargo type (BULK, LIQUID, CONTAINER): ");
        CargoType type = switch (reader.readLine().toUpperCase(Locale.ROOT)) {
            case "BULK" -> CargoType.BULK;
            case "LIQUID" -> CargoType.LIQUID;
            case "CONTAINER" -> CargoType.CONTAINER;
            default -> throw new IOException();
        };

        System.out.print("Arrival date (int): ");
        int date = Integer.parseInt(reader.readLine());

        System.out.print("Unloading time (int): ");
        int time = Integer.parseInt(reader.readLine());

        return new Ship(name, new Cargo(params, type), date, time);
    }
}
