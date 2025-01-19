package sbp.school.kafka.service;

import sbp.school.kafka.config.KafkaConfig;

import java.util.Properties;

public class ThreadListener extends Thread{

    private final TransactionConsumerService service;
    private final String kafkaTopic;

    public ThreadListener(String kafkaTopic) {
        this.service = new TransactionConsumerService(KafkaConfig.getKafkaProperties());
        this.kafkaTopic = kafkaTopic;
    }

    public void listen() {
        service.read(kafkaTopic);
    }

    @Override
    public void run() {
       listen();
    }
}
