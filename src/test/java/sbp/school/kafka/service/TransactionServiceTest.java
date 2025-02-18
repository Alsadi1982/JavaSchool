package sbp.school.kafka.service;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sbp.school.kafka.config.KafkaConfig;
import sbp.school.kafka.entity.TransactionEntity;
import sbp.school.kafka.utils.OperationType;
import sbp.school.kafka.utils.ProducerPartitioner;
import sbp.school.kafka.utils.serializer.TransactionJSONSerializer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class TransactionServiceTest {

    private TransactionService service;
    private TransactionEntity transaction1;
    private TransactionEntity transaction2;
    private ProducerRecord<String, TransactionEntity> record1;
    private ProducerRecord<String, TransactionEntity> record2;


    @BeforeEach
    public void init() {
        transaction1 = new TransactionEntity(OperationType.DEPOSITING, BigDecimal.valueOf(10000), 789456342);
        transaction2 = new TransactionEntity(OperationType.WRITING_OF, BigDecimal.valueOf(500), 789456342);
        record1 = new ProducerRecord<>("kafka-lesson-1", transaction1.getOperationType().name(), transaction1);
        record2 = new ProducerRecord<>("kafka-lesson-1", transaction2.getOperationType().name(), transaction2);
    }

    /**
     * Тест успешного выполнения метода TransactionService№send(ProducerRecord<String, TransactionEntity> record)
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void sendTest_Success() throws ExecutionException, InterruptedException {
        PartitionInfo partitionInfo0 = new PartitionInfo("kafka-lesson-1", 0, null, null, null);
        PartitionInfo partitionInfo1 = new PartitionInfo("kafka-lesson-1", 1, null, null, null);
        List<PartitionInfo> partitionInfoList = new ArrayList<>();
        partitionInfoList.add(partitionInfo0);
        partitionInfoList.add(partitionInfo1);
        Cluster cluster = new Cluster("myClaster", new ArrayList<>(), partitionInfoList, Collections.emptySet(), Collections.emptySet());

        MockProducer<String, TransactionEntity> mockProducer1 = new MockProducer<>(cluster, true, new ProducerPartitioner(), new StringSerializer(), new TransactionJSONSerializer());
        service = new TransactionService(KafkaConfig.getKafkaProperties(), mockProducer1);

        Future<RecordMetadata> future  = service.send(record2);

        Assertions.assertEquals(1, mockProducer1.history().size());
        Assertions.assertEquals(transaction2, mockProducer1.history().get(0).value());
        Assertions.assertEquals(0, future.get().partition());

        Future<RecordMetadata> future2  = service.send(record1);
        Assertions.assertEquals(2, mockProducer1.history().size());
        Assertions.assertEquals(transaction1, mockProducer1.history().get(1).value());
        Assertions.assertEquals(1, future2.get().partition());
    }

    /**
     * Негативный сценарий выполнения метода TransactionService№send(ProducerRecord<String, TransactionEntity> record)
     */
    @Test
    public void sendTest_Fail() {
        MockProducer<String, TransactionEntity> mockProducer = new MockProducer<>(false, new StringSerializer(), new TransactionJSONSerializer());
        service = new TransactionService(KafkaConfig.getKafkaProperties(), mockProducer);
        Future<RecordMetadata> recordMetadataFuture = service.send(record1);

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
