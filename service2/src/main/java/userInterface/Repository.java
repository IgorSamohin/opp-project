package userInterface;

import org.springframework.stereotype.Component;

import java.util.concurrent.FutureTask;

@Component
public interface Repository {
    String getSchedule();

    void saveReport(String s);

    String getReport(String fileName);
}
