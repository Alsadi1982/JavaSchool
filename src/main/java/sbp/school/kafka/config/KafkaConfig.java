package sbp.school.kafka.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Класс отвечает за конфигурацию Kafka
 */
public class KafkaConfig {

    public static Properties getKafkaProperties () {
        Properties props = new Properties();
        try (InputStream input = Files.newInputStream(Paths.get("src/main/resources/kafka.properties"))) {
            props.load(input);
            return props;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


}
