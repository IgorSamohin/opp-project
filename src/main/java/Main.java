import com.fasterxml.jackson.databind.ObjectMapper;
import generator.ship.Ship;
import java.io.File;
import java.io.IOException;
import java.util.List;
import userInterface.UserInterface;

public class Main {
    public static void main(String[] args) throws IOException {
        UserInterface userInterface = new UserInterface(100, 1);
        userInterface.run();

        ObjectMapper mapper = new ObjectMapper();
        List<Ship> schedule = mapper.readValue(new File("src/main/resources/json.json"),
                mapper.getTypeFactory().constructCollectionType(List.class, Ship.class));
    }
}
