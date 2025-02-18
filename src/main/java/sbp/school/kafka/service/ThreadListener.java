package sbp.school.kafka.service;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import sbp.school.kafka.config.KafkaConfig;
import sbp.school.kafka.entity.TransactionEntity;

import java.util.Properties;

public class ThreadListener extends Thread{

    private final TransactionConsumerService service;
    private final String kafkaTopic;

    public ThreadListener(String kafkaTopic) {
        this.service = new TransactionConsumerService(KafkaConfig.getKafkaProperties());
        this.kafkaTopic = kafkaTopic;
    }

    public void listen() {
        service.read(kafkaTopic, new KafkaConsumer<String, TransactionEntity>(KafkaConfig.getKafkaProperties()));
    }

    @Override
    public void run() {
       listen();
    }
}
