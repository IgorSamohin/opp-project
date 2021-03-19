package userInterface;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import generator.ScheduleGenerator;
import generator.cargo.Cargo;
import generator.cargo.CargoType;
import generator.ship.Ship;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import simulator.Manager;

/**
 * Service-2.
 * Gets the schedule from Service-1, checks terminal to additional recordings, runs Service-3 (simulator).
 */
public class UserInterface {
    private final int loaderPerformance;
    private final int amountOfLoaders;

    private final ScheduleGenerator scheduleGenerator;

    public UserInterface(int loaderPerformance, int amountOfLoaders) {
        this.loaderPerformance = loaderPerformance;
        this.amountOfLoaders = amountOfLoaders;
        this.scheduleGenerator = new ScheduleGenerator(loaderPerformance);
    }

    /**
     * In part 2 this method will do GET-request
     */
    private List<Ship> getSchedule() {
        return scheduleGenerator.getSchedule();
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

        return new Ship(name, new Cargo(type, params), date, time);
    }

    public void run() throws JsonProcessingException {
        Scanner in = new Scanner(System.in);
        List<Ship> schedule = this.getSchedule();

        System.out.println("Schedule was generated.");
        while (true) {
            System.out.println("Do you want to enter data? (N|y)");
            String command = in.nextLine();
            if (command.equals("y") || command.equals("Y")) {
                System.out.println("Enter data in format: ");
                try {
                    schedule.add(readShip());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }

        writeScheduleInJsonFile(schedule, "src/main/resources/json.json");

        Manager manager = new Manager();
        try {
            manager.run();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        for (Ship s : manager.getReport()) {
            System.out.println(s);
        }
    }
}
