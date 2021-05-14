package userInterface;

import java.util.List;

import org.springframework.stereotype.Component;
import userInterface.ship.Ship;

@Component
public interface Repository {
    List<Ship> getSchedule();

    String getReport(String s);
}
