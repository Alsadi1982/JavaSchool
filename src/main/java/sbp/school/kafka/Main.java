package sbp.school.kafka;

import sbp.school.kafka.config.KafkaConfig;
import sbp.school.kafka.config.LoggerConfig;
import sbp.school.kafka.service.ThreadListener;
import sbp.school.kafka.service.TransactionConsumerService;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;


public class Main {
    static {
        LoggerConfig.getLoggerConfig();
    }

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        if (args.length != 1) {
            System.out.println("Пожалуйста укажите название топика!");
            System.out.println("Пример: java -jar consumer.jar topic_name");
            LOGGER.warning("Не указано название топика!");
        }else {
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            executorService.submit(new ThreadListener(args[0])).get();
        }
    }
}
