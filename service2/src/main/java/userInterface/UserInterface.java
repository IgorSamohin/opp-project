package userInterface;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import userInterface.cargo.Cargo;
import userInterface.cargo.CargoType;
import userInterface.ship.Ship;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
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
    List<Ship> ships;

    /**
     * Entry point
     */
    @GetMapping("/start")
    @ResponseBody
    public List<Ship> startWork() {
        ships = repository.getSchedule();
        String report = repository.getReport("json.json");
        return ships;
    }

    /**
     * Endpoint to receive results from service 3
     *
     * @param report - results
     */
    @PostMapping("/results")
    public void getResults(@RequestParam String report) {
        System.out.println(report);
    }

    /**
     * Endpoint to return data to service 3
     *
     * @param filename - name of the file with data
     */
    @GetMapping("/schedule")
    @ResponseBody
    public List<Ship> getData(@RequestParam(value = "filename") String filename) {
        return ships;
    }

    /**
     * Transform schedule to json objects and writes it to a file
     */
    private void writeScheduleInJsonFile(List<Ship> ships, String fileName) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonSchedule = mapper.writeValueAsString(ships);
        File file = new File(fileName);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(jsonSchedule);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @return a ship read from terminal or error
     */
    private Ship readShip() throws IOException { //todo добавить проверки на правильный формат ввода, на неповторяемость введенного имени
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

//    public void run() throws JsonProcessingException {
//        Scanner in = new Scanner(System.in);
//        List<Ship> schedule = this.getSchedule();
//
//        System.out.println("Schedule was generated.");
//        while (true) {
//            System.out.println("Do you want to enter data? (N|y)");
//            String command = in.nextLine();
//            if (command.equals("y") || command.equals("Y")) {
//                System.out.println("Enter data in format: ");
//                try {
//                    schedule.add(readShip());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                break;
//            }
//        }
//
//        writeScheduleInJsonFile(schedule, "src/main/resources/json.json");
//
//        Manager manager = new Manager();
//        try {
//            manager.run();
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        List<Ship> unloadingHistory = manager.getReport().getUnloadingHistory();
//        for (Ship s : unloadingHistory) {
//            System.out.println(s);
//        }
//    }
}
