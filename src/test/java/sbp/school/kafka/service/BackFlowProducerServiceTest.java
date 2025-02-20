package sbp.school.kafka.service;

import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sbp.school.kafka.config.KafkaConfig;
import sbp.school.kafka.entity.HashSumDto;
import sbp.school.kafka.utils.deserializer.HashSumJSONSerializer;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class BackFlowProducerServiceTest {

    private BackFlowProducerService service;
    private  HashSumDto hashSumDto;
    private ProducerRecord<String, HashSumDto> record;

    @BeforeEach
    public void init() {
        hashSumDto = new HashSumDto(123456789, Timestamp.valueOf(LocalDateTime.now().minusMinutes(10)));
        record = new ProducerRecord<>("hashSum-topic", hashSumDto);
    }

    /**
     * Успешный сценарий отправки сообщения
     */
    @Test
    public void sendTest_Success() {
        MockProducer<String, HashSumDto> mockProducer = new MockProducer<>(
                true, new StringSerializer(), new HashSumJSONSerializer());

        service = new BackFlowProducerService(KafkaConfig.getKafkaProperties(), mockProducer);
        Future<RecordMetadata> recordMetadataFuture = service.send(record);

        Assertions.assertEquals(1, mockProducer.history().size());
        Assertions.assertEquals(hashSumDto, mockProducer.history().get(0).value());
    }

    @Test
    public void sendTest_Fail() {
        MockProducer<String, HashSumDto> mockProducer = new MockProducer<>(false, new StringSerializer(), new HashSumJSONSerializer());
        service = new BackFlowProducerService(KafkaConfig.getKafkaProperties(), mockProducer);
        Future<RecordMetadata> recordMetadataFuture = service.send(record);

        RuntimeException e = new RuntimeException("some error");
        mockProducer.errorNext(e);

        Assertions.assertThrows(ExecutionException.class, recordMetadataFuture::get);

        try {
            recordMetadataFuture.get();
        } catch (ExecutionException | InterruptedException ex) {
            Assertions.assertEquals(e, ex.getCause());
        }
        Assertions.assertTrue(recordMetadataFuture.isDone());
    }


}
