package sbp.school.kafka;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import sbp.school.kafka.config.BackFlowKafkaConfig;
import sbp.school.kafka.config.LoggerConfig;
import sbp.school.kafka.entity.HashSumDto;
import sbp.school.kafka.service.ThreadListener;

import java.util.concurrent.*;


public class Main {
    static {
        LoggerConfig.getLoggerConfig();
    }
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        KafkaConsumer<String, HashSumDto> consumer = new KafkaConsumer<>(BackFlowKafkaConfig.getKafkaProperties());
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(new ThreadListener(BackFlowKafkaConfig.getKafkaProperties().getProperty("kafka.hashSum.topic.name"), consumer)).get();

    }
}
