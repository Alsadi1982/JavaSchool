package sbp.school.kafka;

import sbp.school.kafka.config.BackFlowKafkaConfig;
import sbp.school.kafka.config.LoggerConfig;
import sbp.school.kafka.service.ThreadListener;

import java.util.concurrent.*;


public class Main {
    static {
        LoggerConfig.getLoggerConfig();
    }
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(new ThreadListener(BackFlowKafkaConfig.getKafkaProperties().getProperty("kafka.hashSum.topic.name"))).get();

    }
}
