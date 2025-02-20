package sbp.school.kafka.service;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sbp.school.kafka.entity.HashSumDto;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class BackFlowConsumerService {

    private static final Logger log = LoggerFactory.getLogger(BackFlowConsumerService.class);
    private final Properties props;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private Consumer<String, HashSumDto> consumer;

    public BackFlowConsumerService(Properties props) {
        this.props = props;
    }

    public HashSumDto read(String topicName, Consumer cons) {
        consumer = cons;
        consumer.subscribe(Collections.singletonList(topicName));
        ConsumerRecord<String, HashSumDto> currentRecord = null;
        try {
            ConsumerRecords<String, HashSumDto> consumerRecords = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, HashSumDto> record : consumerRecords) {
                currentOffsets.put(new TopicPartition(record.topic(), record.partition()),
                        new OffsetAndMetadata(record.offset() + 1, "some metadata"));
                HashSumDto hashSumDto = record.value();
                log.info("hashSum = {}, fromData = {}", hashSumDto.getHashSum(), hashSumDto.getFromDate());
                currentRecord = record;
                return hashSumDto;
            }
        }catch (WakeupException e) {
            log.info("Shuttinng down!");
        } catch (Exception ex) {
            if (currentRecord != null) {
                log.error("Сбой получения сообщения! " + getErrorMessage(currentRecord, ex), ex);
            } else {
               log.error("Сбой получения сообщения! ConsumerRecord = null!", ex);
            }
            throw new RuntimeException(ex.getMessage(), ex);
        }
        return new HashSumDto();
    }

    private String getErrorMessage(ConsumerRecord<String, HashSumDto> record, Exception ex) {
        return String.format("offset = %d, partition = %d, topic = %s, Exception: %s",
                record.offset(), record.partition(), record.topic(), ex.getMessage());
    }

    public void stop(){
        consumer.wakeup();
    }
}