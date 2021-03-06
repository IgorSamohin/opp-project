package simulator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import generator.ship.Ship;
import java.io.File;
import java.io.IOException;
import static java.lang.Math.min;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Phaser;

/**
 * Service-3.
 * Takes schedule from Service-2, create delays and early arrivals, do the simulation, return data.
 */
public class Simulator {
    private int amountOfLoaders = 5;
    private int loaderPerformance = 100;
    Random random = new Random();
    private final int maxDelay = 10_080;
    private final int maxTime = 43_200;
    private List<Thread> loaders = new ArrayList<>();
    private List<Worker> workers = new ArrayList<>();
    private List<Ship> report = new ArrayList<>();
    private ConcurrentLinkedQueue<Ship> arrivedShips = new ConcurrentLinkedQueue<>();
    private int currentTime = 0;
    private boolean canWork = false;

    public List<Ship> getReport() {
        return report;
    }

    private void makeDelays(List<Ship> ships) {
        for (Ship ship : ships) {
            int delay = random.nextInt(maxDelay * 2) - maxDelay;
            if(delay < 0){
                delay = -min(ship.getArrivalDate(), -delay);
            }
            ship.increaseArrivalDate(delay);
        }
    }

    public void run() throws IOException, BrokenBarrierException, InterruptedException {
        List<Ship> schedule = this.getSchedule();
        List<Ship> actualSchedule = new ArrayList<>(schedule);

        this.makeDelays(actualSchedule);

        actualSchedule.sort(Comparator.comparingInt(Ship::getArrivalDate));

        this.startSimulation(schedule, actualSchedule);
    }

    private void startSimulation(List<Ship> schedule, List<Ship> actualSchedule) {
        Phaser phaser = new Phaser(amountOfLoaders + 1);

        for (int i = 0; i < amountOfLoaders; i++) {
            workers.add(new Worker(arrivedShips, loaderPerformance, currentTime));

            loaders.add(new Thread(() -> {
                int takenWorker = -1;

                while (true) {

                    phaser.arriveAndAwaitAdvance();

                    if ((takenWorker >= 0) && (workers.get(takenWorker).isUnloaded())) {
                        workers.get(takenWorker).release();
                        takenWorker = -1;
                    }

                    if (takenWorker < 0) {
                        for (int j = 0; j < amountOfLoaders; j++) {
                            if (workers.get(j).takePlace()) {
                                takenWorker = j;
                                break;
                            }
                        }
                    }

                    if (takenWorker >= 0) {
                        workers.get(takenWorker).work();
                    }

                    phaser.arriveAndAwaitAdvance();
                }
            }));
        }

        for (int i = 0; i < amountOfLoaders; i++) {
            loaders.get(i).start();
        }

        while (currentTime != maxTime) {
            while (!(actualSchedule.isEmpty()) && (actualSchedule.get(0).getArrivalDate() == currentTime)) {
                arrivedShips.add(actualSchedule.remove(0));
            }

            for (int i = 0; i < amountOfLoaders; i++) {
                Ship ship = workers.get(i).update();
                if (ship != null) {
                    report.add(ship);
                }
            }

            canWork = true;
            phaser.arriveAndAwaitAdvance();

            ++currentTime;
            phaser.arriveAndAwaitAdvance();
        }

        for (int i = 0; i < amountOfLoaders; i++) {
            loaders.get(i).interrupt();//todo потоки почему-то не гасятся
        }

    }

    /**
     * In part 2 this method will do GET-request to service-2
     */
    private List<Ship> getSchedule() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, Ship.class);
        return mapper.readValue(new File("src/main/resources/json.json"), collectionType);
    }
}
