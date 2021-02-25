package generator;

/**
 * Main class of Service-1. Use to get random schedule on 30 days
 */
public class Schedule {
    private final int loaderPerformance;
    private final int amountOfLoaders;

    public Schedule(int loaderPerformance, int amountOfLoaders) {
        this.loaderPerformance = loaderPerformance;
        this.amountOfLoaders = amountOfLoaders;
    }

    public String getSchedule(){
        return "";
    }
}
