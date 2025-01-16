package sbp.school.kafka.service;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import sbp.school.kafka.entity.TransactionEntity;

import java.time.Duration;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс отвечающий за чтение и обработку сообщений
 */
public class TransactionConsumerService {

    private static final Logger LOGGER = Logger.getLogger(TransactionConsumerService.class.getName());
    private final Properties props;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    int counter = 0;

    public TransactionConsumerService(Properties props) {
        this.props = props;
    }

    public void read() {

        KafkaConsumer<String, TransactionEntity> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("kafka-lesson-1"));
        ConsumerRecord<String, TransactionEntity> currentRecord = null;
        try {
            while (true) {
                ConsumerRecords<String, TransactionEntity> consumerRecords = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, TransactionEntity> record : consumerRecords) {
                    String successMessage = String.format("Успешное получение сообщения! offset = %d, partition = %d, topic = %s, message = %s",
                            record.offset(), record.partition(), record.topic(), record.value());
                    LOGGER.info(successMessage);
                    currentOffsets.put(new TopicPartition(record.topic(), record.partition()),
                            new OffsetAndMetadata(record.offset() + 1, "some metadata"));
                    if (counter % 100 == 0) {
                        consumer.commitSync(currentOffsets, null);
                    }
                    counter++;
                    currentRecord = record;
                }

            }
        } catch (Exception ex) {
            if (currentRecord != null) {
                LOGGER.log(Level.WARNING, "Сбой получения сообщения! " + getErrorMessage(currentRecord, ex), ex);
            } else {
                    LOGGER.log(Level.WARNING, "Сбой получения сообщения! ConsumerRecord = null!", ex);
            }
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            try {
                consumer.commitSync(currentOffsets, null);
            } catch (Exception e) {
                if (currentRecord != null) {
                    LOGGER.log(Level.WARNING, "Commit error! " + getErrorMessage(currentRecord, e), e);
                } else {
                    LOGGER.log(Level.WARNING, "Commit error! ConsumerRecord = null!", e);
                }
            }
        }
    }

    private String getErrorMessage(ConsumerRecord<String, TransactionEntity> record, Exception ex) {
        return String.format("offset = %d, partition = %d, topic = %s, Exception: %s",
                record.offset(), record.partition(), record.topic(), ex.getMessage());
    }
}
