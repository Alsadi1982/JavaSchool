package sbp.school.kafka.connect;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.kafka.common.config.ConfigDef.NO_DEFAULT_VALUE;

public class CustomDBStreamSourceConnector extends SourceConnector {

    private static final Logger log = LoggerFactory.getLogger(CustomDBStreamSourceConnector.class);
    public static final String TOPIC_CONFIG = "topic";
    public static final String DATABASE_URL = "jdbc:h2:tcp://localhost/~/test";
    public static final String TASK_BATCH_SIZE_CONFIG = "batch.size";

    public static final int DEFAULT_TASK_BATCH_SIZE = 2000;

    static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(DATABASE_URL, Type.STRING, null, Importance.HIGH, "Source DB URL")
            .define(TOPIC_CONFIG, Type.STRING, NO_DEFAULT_VALUE, new ConfigDef.NonEmptyString(), Importance.HIGH, "The topic to publish data to")
            .define(TASK_BATCH_SIZE_CONFIG, Type.INT, DEFAULT_TASK_BATCH_SIZE, Importance.LOW,
                    "The maximum number of records the source task can read from the file each time it is polled");

    private Map<String, String> props;


    @Override
    public void start(Map<String, String> map) {
        this.props = map;
        AbstractConfig config = new AbstractConfig(CONFIG_DEF, props);
        String urlPath = config.getString(DATABASE_URL);
        log.info("Starting db source connector reading from {}", urlPath);
    }

    @Override
    public Class<? extends Task> taskClass() {
        return CustomDBStreamSourceTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int i) {
        ArrayList<Map<String, String>> configs = new ArrayList<>();
        // Only one input stream makes sense.
        configs.add(props);
        return configs;
    }

    @Override
    public void stop() {

    }

    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }

    @Override
    public String version() {
        return AppInfoParser.getVersion();
    }
}
