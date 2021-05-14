package simulator.util;

import simulator.cargo.CargoType;
import simulator.ship.Ship;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class SimulatorsFactory {
    public Map<CargoType, Simulator> createSimulators(List<Ship> ships, int amountOfLoaders, int loaderPerformance) {
        final EnumMap<CargoType, List<Ship>> lists = new EnumMap<>(CargoType.class);
        for (Ship s : ships) {
            List<Ship> currShips = lists.computeIfAbsent(s.getCargo().getCargoType(), k -> new ArrayList<>());
            currShips.add(s);
        }

        EnumMap<CargoType, Simulator> simulators = new EnumMap<>(CargoType.class);
        for (CargoType type : CargoType.values()) {
            simulators.put(type, new Simulator(lists.get(type), amountOfLoaders, loaderPerformance));
        }
        return simulators;
    }
}
