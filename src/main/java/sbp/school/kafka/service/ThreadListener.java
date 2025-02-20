package sbp.school.kafka.service;

import org.apache.kafka.clients.consumer.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sbp.school.kafka.config.BackFlowKafkaConfig;
import sbp.school.kafka.config.KafkaConfig;
import sbp.school.kafka.dao.BackFlowJDBCService;
import sbp.school.kafka.entity.HashSumDto;
import sbp.school.kafka.entity.TransactionEntity;

import java.util.List;
import java.util.Objects;

public class ThreadListener extends Thread{

    private static final Logger log = LoggerFactory.getLogger(ThreadListener.class);
    private final TransactionService transactionService;
    private final BackFlowJDBCService JDBCService;
    private final BackFlowConsumerService backFlowConsumerService;
    private final String kafkaTopic;
    private Consumer<String, HashSumDto> consumer;

    public ThreadListener(String kafkaTopic, Consumer<String, HashSumDto> consumer) {
        this.backFlowConsumerService = new BackFlowConsumerService(BackFlowKafkaConfig.getKafkaProperties());
        this.JDBCService = new BackFlowJDBCService();
        this.kafkaTopic = kafkaTopic;
        this.transactionService = new TransactionService(KafkaConfig.getKafkaProperties());
        this.consumer = consumer;
    }

    public void listen() {
        while (true) {
            HashSumDto hashSum = backFlowConsumerService.read(kafkaTopic, consumer);
            long hahSumFromTopic = hashSum.getHashSum();
            List<TransactionEntity> transactionList = JDBCService.getListByPeriod(hashSum.getFromDate());
            long hashSumFromDB = transactionList.stream()
                    .map(TransactionEntity::getId)
                    .reduce(0, Integer::sum);
            log.info("hahSumFromTopic = {}, hashSumFromDB = {}, equals = {}", hahSumFromTopic, hashSumFromDB, Objects.equals(hahSumFromTopic, hashSumFromDB));
            if (hahSumFromTopic != hashSumFromDB) {
                for (TransactionEntity transaction : transactionList) {
                    transactionService.reSend(transaction, BackFlowKafkaConfig.getKafkaProperties().getProperty("kafka.producer.topic.name"));
                }
            }
        }
    }

    @Override
    public void run() {
       listen();
    }
}
