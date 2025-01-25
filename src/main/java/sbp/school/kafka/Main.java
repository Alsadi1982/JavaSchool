package sbp.school.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sbp.school.kafka.config.KafkaConfig;
import sbp.school.kafka.config.LoggerConfig;
import sbp.school.kafka.service.BackFlowThreadListener;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Main {
    static {
        LoggerConfig.getLoggerConfig();
    }

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args){
        String topicName = KafkaConfig.getKafkaProperties().getProperty("kafka.hashSum.topic.name");
            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
            executorService.scheduleWithFixedDelay(new BackFlowThreadListener(topicName), 0, 10, TimeUnit.MINUTES);
    }
}
