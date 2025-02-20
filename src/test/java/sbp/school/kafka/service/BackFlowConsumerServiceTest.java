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
import sbp.school.kafka.entity.HashSumDto;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;

public class BackFlowConsumerServiceTest {

    private MockConsumer<String, HashSumDto> consumer;
    private BackFlowConsumerService service;

    private static final String TOPIC = "topic";
    public static final int PARTITION = 0;
    private static final HashSumDto entity = new HashSumDto(123456789, Timestamp.valueOf(LocalDateTime.now().minusMinutes(10)));

    @BeforeEach
    public void init() {
        consumer = new MockConsumer<>(OffsetResetStrategy.EARLIEST);
        service = new BackFlowConsumerService(KafkaConfig.getKafkaProperties());
    }

    /**
     * Успешный сценарий чтения сообщения консюмером
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void readTest_Success() throws NoSuchFieldException, IllegalAccessException {
        consumer.schedulePollTask(() -> {
            consumer.rebalance(Collections.singletonList(new TopicPartition(TOPIC, PARTITION)));
            consumer.addRecord(new ConsumerRecord<>(TOPIC, PARTITION, 0L, null, entity));
        });

        consumer.schedulePollTask(() -> service.stop());

        HashMap<TopicPartition, Long> startingOffsets = new HashMap<>();
        TopicPartition tp = new TopicPartition(TOPIC, PARTITION);
        startingOffsets.put(tp, 0L);
        consumer.updateBeginningOffsets(startingOffsets);

        service.read(TOPIC, consumer);

        Field field = BackFlowConsumerService.class.getDeclaredField("currentOffsets");
        field.setAccessible(true);
        HashMap<TopicPartition, OffsetAndMetadata> map = (HashMap<TopicPartition, OffsetAndMetadata>) field.get(service);
        long currentOffset = map.get(tp).offset();

        Assertions.assertEquals(1, currentOffset);
    }

    @Test
    public void readTest_Fail() {
        consumer.schedulePollTask(() -> {
            consumer.setPollException(new KafkaException("poll exception"));
        });
        consumer.schedulePollTask(() -> service.stop() );

        HashMap<TopicPartition, Long> startingOffsets = new HashMap<>();
        TopicPartition tp = new TopicPartition(TOPIC, PARTITION);
        startingOffsets.put(tp, 0L);
        consumer.updateBeginningOffsets(startingOffsets);

        Assertions.assertThrows(RuntimeException.class, () -> service.read(TOPIC, consumer));
    }
}
