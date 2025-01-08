package sbp.school.kafka;

import org.apache.kafka.clients.producer.ProducerRecord;
import sbp.school.kafka.config.KafkaConfig;
import sbp.school.kafka.config.LoggerConfig;
import sbp.school.kafka.entity.TransactionEntity;
import sbp.school.kafka.service.ThreadListener;
import sbp.school.kafka.service.TransactionService;
import sbp.school.kafka.utils.OperationType;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    static {
        LoggerConfig.getLoggerConfig();
    }
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(new ThreadListener()).get();

    }
}
