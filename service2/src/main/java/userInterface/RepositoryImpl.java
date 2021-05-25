package userInterface;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Component
public class RepositoryImpl implements Repository {
    @Autowired
    RestTemplate template;

    private static final String SERVICE_ONE_URL = "http://localhost:8081/schedule";
    private static final String SERVICE_THREE_URL = "http://localhost:8083/report?filename=%s";
    private static final String SCHEDULE_FILE_NAME = "json.json";
    private static final String REPORT_FILE_NAME = "report.json";

    @Override
    public String getSchedule() throws IOException {
        String s = template.getForObject(SERVICE_ONE_URL, String.class);
        writeInFile(s, SCHEDULE_FILE_NAME);
        return SCHEDULE_FILE_NAME;
    }

    @Override
    public void saveReport(String s) throws IOException {
        writeInFile(s, REPORT_FILE_NAME);
    }

    @Override
    public String notify(String fileName) {
        template.getForObject(String.format(SERVICE_THREE_URL, SCHEDULE_FILE_NAME), String.class);
        return HttpStatus.OK.toString();
    }

    @Override
    public List<Report> getReport(String report) throws IOException {
        String reports = readFromFile(report);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readerForListOf(Report.class).readValue(reports);
    }

    public List<Report> getReport() throws IOException {
        return getReport(REPORT_FILE_NAME);
    }

    @Override
    public String getScheduleFrom(String filename) throws IOException {
        return readFromFile(filename);
    }

    private String readFromFile(String fileName) throws IOException {
        if (!Files.exists(Path.of(fileName))) {
            return HttpStatus.NOT_FOUND.toString();
        }

        try (FileChannel fileChannel = FileChannel.open(Path.of(fileName), StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
            fileChannel.read(buffer);
            buffer.position(0);
            int size = buffer.getInt();

            ByteBuffer string = ByteBuffer.allocate(size);
            fileChannel.read(string);
            string.position(0);
            return new String(string.array());
        }
    }

    private void writeInFile(String s, String fileName) throws IOException {
        Files.deleteIfExists(Path.of(fileName));

        try (FileChannel fileChannel = FileChannel.open(Path.of(fileName),
                StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW)) {
            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
            buffer.putInt(s.length());
            buffer.position(0);
            fileChannel.write(buffer);

            fileChannel.write(ByteBuffer.wrap(s.getBytes(StandardCharsets.UTF_8)));
        }
    }

    public boolean hasFile(String filename) {
        return new File(filename).exists();
    }
}
