package sbp.school.kafka;

import org.apache.kafka.clients.producer.ProducerRecord;
import sbp.school.kafka.config.KafkaConfig;
import sbp.school.kafka.config.LoggerConfig;
import sbp.school.kafka.entity.TransactionEntity;
import sbp.school.kafka.service.TransactionService;
import sbp.school.kafka.utils.OperationType;

import java.math.BigDecimal;

public class Main {
    static {
        LoggerConfig.getLoggerConfig();
    }
    public static void main(String[] args) {
        TransactionEntity transaction1 = new TransactionEntity(OperationType.DEPOSITING, BigDecimal.valueOf(10000), 789456342);
        TransactionEntity transaction2 = new TransactionEntity(OperationType.WRITING_OF, BigDecimal.valueOf(500), 789456342);
        ProducerRecord<String, TransactionEntity> record1 = new ProducerRecord<>("kafka-lesson-1", transaction1.getOperationType().name(), transaction1);
        ProducerRecord<String, TransactionEntity> record2 = new ProducerRecord<>("kafka-lesson-1", transaction2.getOperationType().name(), transaction2);
        TransactionService transactionService = new TransactionService(KafkaConfig.getKafkaProperties());
        transactionService.send(record1);
        transactionService.send(record2);
    }
}
