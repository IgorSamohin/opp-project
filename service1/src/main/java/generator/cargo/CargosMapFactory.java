package generator.cargo;

import java.util.EnumMap;
import java.util.Map;

public class CargosMapFactory {
    public Map<CargoType, Integer> createCargosMap() {
        CargoType[] values = CargoType.values();
        EnumMap<CargoType, Integer> map = new EnumMap<>(CargoType.class);
        for (CargoType type : values) {
            map.put(type, 0);
        }
        return map;
    }
}
