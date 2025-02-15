package sbp.school.kafka.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Класс отвечает за конфигурацию
 */
public class AppConfig {
    static final Logger log = LoggerFactory.getLogger(AppConfig.class);

    public static Properties getAppProperties () {
        Properties props = new Properties();
        try (InputStream input = Files.newInputStream(Paths.get("src/main/resources/app.properties"))) {
            props.load(input);
            log.info("Successful reading of properties!");
            return props;
        } catch (IOException e) {
            log.error("Something wrong with reading of properties!!!", e);
            throw new RuntimeException(e.getMessage());
        }
    }


}
