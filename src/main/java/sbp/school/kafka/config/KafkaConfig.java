package sbp.school.kafka.config;

import sbp.school.kafka.service.TransactionService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Класс отвечает за конфигурацию Kafka
 */
public class KafkaConfig {

    private static final Logger LOGGER = Logger.getLogger(KafkaConfig.class.getName());

    public static Properties getKafkaProperties () {
        Properties props = new Properties();
        try (InputStream input = Files.newInputStream(Paths.get("src/main/resources/kafka.properties"))) {
            props.load(input);
            return props;
        } catch (IOException e) {
            LOGGER.warning("Could not setup kafka configuration: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }


}
