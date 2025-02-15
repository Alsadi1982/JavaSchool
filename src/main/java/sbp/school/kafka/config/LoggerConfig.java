package sbp.school.kafka.config;

import sbp.school.kafka.Main;

import java.io.IOException;
import java.util.logging.LogManager;

/**
 * Класс отвечает за конфигурацию логера
 */
public class LoggerConfig {

    public static void getLoggerConfig() {
        try {
            LogManager.getLogManager().readConfiguration(
                    Main.class.getResourceAsStream("/logging.properties"));
        } catch (IOException e) {
            throw  new RuntimeException("Could not setup logger configuration: " + e.getMessage());
        }
    }
}
