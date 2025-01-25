package sbp.school.kafka.config;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Класс отвечает за конфигурацию Kafka
 */
public class KafkaConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaConfig.class);

    public static Properties getKafkaProperties () {
        Properties props = new Properties();
        try (InputStream input = Files.newInputStream(Paths.get("src/main/resources/kafka.properties"))) {
            props.load(input);
            return props;
        } catch (IOException e) {
            log.warn("Could not setup kafka configuration: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }


}
