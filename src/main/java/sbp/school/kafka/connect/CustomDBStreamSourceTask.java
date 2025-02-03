package sbp.school.kafka.connect;

import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;

import java.util.List;
import java.util.Map;

public class CustomDBStreamSourceTask extends SourceTask {
    @Override
    public void start(Map<String, String> map) {

    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        return List.of();
    }

    @Override
    public void stop() {

    }

    @Override
    public String version() {
        return "";
    }
}
