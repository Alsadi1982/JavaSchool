package sbp.school.kafka.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import sbp.school.kafka.entity.TransactionEntity;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class TransactionConsumerService {

    private static final Logger LOGGER = Logger.getLogger(TransactionConsumerService.class.getName());
    private final Properties props;

    public TransactionConsumerService(Properties props) {
        this.props = props;
    }

    public void read() {

        KafkaConsumer<String, TransactionEntity> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(List.of("kafka-lesson-1"));

        while (true) {
            try {
                ConsumerRecords<String, TransactionEntity> consumerRecords = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, TransactionEntity> record : consumerRecords) {
                    System.out.println("offset = " + record.offset());
                    System.out.println("partition = " + record.partition());
                    System.out.println("topic = " + record.topic());
//                    System.out.println("group.id = " + consumer.groupMetadata().groupId());
                    System.out.println("message = " + record.value());
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
