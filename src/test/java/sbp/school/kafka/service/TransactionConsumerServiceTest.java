package sbp.school.kafka.service;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sbp.school.kafka.config.KafkaConfig;
import sbp.school.kafka.entity.TransactionEntity;
import sbp.school.kafka.utils.OperationType;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;

public class TransactionConsumerServiceTest {

    private MockConsumer<String, TransactionEntity> consumer;
    private TransactionConsumerService service;

    private static final String TOPIC = "topic";
    public static final int PARTITION = 0;
    private static final TransactionEntity entity = new TransactionEntity(OperationType.DEPOSITING, BigDecimal.valueOf(2000L), 12345);


    @BeforeEach
    void init() {
        consumer = new MockConsumer<>(OffsetResetStrategy.EARLIEST);
        service = new TransactionConsumerService(KafkaConfig.getKafkaProperties());
    }

    /**
     * Проверка успешного выполнения метода TransactionConsumerService#read(String kafkaTopic, Consumer cons)
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void readTest_Success() throws NoSuchFieldException, IllegalAccessException {


        consumer.schedulePollTask(() -> {
            consumer.rebalance(Collections.singletonList(new TopicPartition(TOPIC, PARTITION)));
            consumer.addRecord(new ConsumerRecord<>(TOPIC, PARTITION, 0L, null, entity));
        });
        consumer.schedulePollTask(() -> service.stop() );

        HashMap<TopicPartition, Long> startingOffsets = new HashMap<>();
        TopicPartition tp = new TopicPartition(TOPIC, PARTITION);
        startingOffsets.put(tp, 0L);
        consumer.updateBeginningOffsets(startingOffsets);

        service.read(TOPIC, consumer);

        Field field = TransactionConsumerService.class.getDeclaredField("currentOffsets");
        field.setAccessible(true);
        HashMap<TopicPartition, OffsetAndMetadata> map = (HashMap<TopicPartition, OffsetAndMetadata>) field.get(service);
        long currentOffset = map.get(tp).offset();

        Assertions.assertEquals(1, currentOffset);
        Assertions.assertTrue(consumer.closed());
    }

    /**
     * Проверка того, что метод TransactionConsumerService#read(String kafkaTopic, Consumer cons) выкидывает исключение
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void readTest_Exception() throws NoSuchFieldException, IllegalAccessException {

        consumer.schedulePollTask(() -> {
            consumer.setPollException(new KafkaException("poll exception"));
        });
        consumer.schedulePollTask(() -> service.stop() );

        HashMap<TopicPartition, Long> startingOffsets = new HashMap<>();
        TopicPartition tp = new TopicPartition(TOPIC, PARTITION);
        startingOffsets.put(tp, 0L);
        consumer.updateBeginningOffsets(startingOffsets);

        Assertions.assertThrows(RuntimeException.class, () -> service.read(TOPIC, consumer));

        Assertions.assertTrue(consumer.closed());

    }

}
