package simulator;

import java.util.List;

import simulator.ship.Ship;

@org.springframework.stereotype.Repository
public interface Repository {
    List<Ship> getSchedule();
}
