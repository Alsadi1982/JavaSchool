package sbp.school.kafka.service;

import sbp.school.kafka.config.KafkaConfig;

import java.util.Properties;

public class ThreadListener extends Thread{

    private final TransactionConsumerService service;

    public ThreadListener() {
        this.service = new TransactionConsumerService(KafkaConfig.getKafkaProperties());
    }

    public void listen() {
        service.read();
    }

    @Override
    public void run() {
       listen();
    }
}
