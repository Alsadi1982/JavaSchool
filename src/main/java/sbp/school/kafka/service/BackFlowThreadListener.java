package sbp.school.kafka.service;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sbp.school.kafka.config.KafkaConfig;
import sbp.school.kafka.entity.HashSumDto;
import sbp.school.kafka.entity.TransactionEntity;
import sbp.school.kafka.utils.dao.BackFlowProducerDao;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class BackFlowThreadListener extends  Thread{

    private static final Logger log = LoggerFactory.getLogger(BackFlowThreadListener.class);
    private final BackFlowProducerService service;
    private final String kafkaTopic;
    private final BackFlowProducerDao dao;


    public BackFlowThreadListener(String kafkaTopic) {
        this.service = new BackFlowProducerService(KafkaConfig.getKafkaProperties());
        this.kafkaTopic = kafkaTopic;
        this.dao = new BackFlowProducerDao();
    }

    public void listen() {
        long delayInterval = Long.parseLong(KafkaConfig.getKafkaProperties().getProperty("delay.interval"));
        Timestamp fromDate = Timestamp.valueOf(LocalDateTime.now().minusMinutes(delayInterval));
        List<TransactionEntity> transactionList = dao.getListByPeriod(fromDate);
        long hashSumFromDB = transactionList.stream()
                .map(TransactionEntity::getId)
                .reduce(0, Integer::sum);
        log.info("hashSum = {}, fromDate = {}", hashSumFromDB, fromDate);
        ProducerRecord<String, HashSumDto> record = new ProducerRecord<>(kafkaTopic, new HashSumDto(hashSumFromDB, fromDate));
        if (hashSumFromDB != 0) {
            service.send(record);
        }
    }

    @Override
    public void run() {
        listen();
    }
}
