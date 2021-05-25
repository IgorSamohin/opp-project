package userInterface;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public interface Repository {
    /**
     * Get schedule from service-1 and store it in file
     *
     * @return filename
     */
    String getSchedule() throws IOException;

    void saveReport(String s) throws IOException;

    /**
     * Notify service-3 that we have schedule stored in file with filename
     *
     * @param fileName name of the file with the schedule
     * @return
     */
    String notify(String fileName);

    List<Report> getReport(String report) throws IOException;

    String getScheduleFrom(String filename) throws IOException;
}
