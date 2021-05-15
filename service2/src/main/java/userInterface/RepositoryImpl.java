package userInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import userInterface.ship.Ship;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledFuture;

@Component
public class RepositoryImpl implements Repository {
    @Autowired
    RestTemplate template;

    private static final String SERVICE_ONE_URL = "http://localhost:8081/schedule";
    private static final String SERVICE_THREE_URL = "http://localhost:8083/report?filename=%s";
    private static final String SCHEDULE_FILE_NAME = "json.json";
    private static final String REPORT_FILE_NAME = "report.json";
    private boolean haveResults = false;

    @Override
    public String getSchedule() {
        String s = template.getForObject(SERVICE_ONE_URL, String.class);
        writeInFile(s, SCHEDULE_FILE_NAME);
        return s;
    }

    @Override
    public void saveReport(String s) {
        writeInFile(s, REPORT_FILE_NAME);
        haveResults = true;
    }

    @Override
    public String getReport(String fileName) {
        haveResults = false;
        template.getForObject(String.format(SERVICE_THREE_URL, SCHEDULE_FILE_NAME), String.class);
        return "";
    }

    public String readFromFile(String fileName){
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            return in.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void writeInFile(String s, String fileName) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasFile(String filename) {
        return new File(filename).exists();
    }

    public boolean haveResults() {
        return haveResults;
    }
}
