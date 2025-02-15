package sbp.school.kafka.service;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sbp.school.kafka.entity.HashSumDto;

import java.util.Properties;
import java.util.concurrent.Future;


public class BackFlowProducerService {

    private static final Logger log = LoggerFactory.getLogger(BackFlowProducerService.class);
    private final Properties props;

    public BackFlowProducerService(Properties props) {
        this.props = props;
    }

    public void send(ProducerRecord<String, HashSumDto> record){
        KafkaProducer<String, HashSumDto> producer = null;
        try  {
            producer = new KafkaProducer<>(props);
            Future<RecordMetadata> future = producer.send(record, ((metadata, exception) -> {
                if (exception != null) {
                    String errorMessage = String.format("Сбой передачи сообщения! offset = %d, partition = %d, Exception: %s",
                            metadata.offset(), metadata.partition(), exception.getMessage());
                    log.warn(errorMessage, exception);
                } else {
                    String successMessage = String.format("Успешная отправка сообщения! offset = %d, partition = %d, topic = %s",
                            metadata.offset(), metadata.partition(), metadata.topic());
                    log.info(successMessage);
                }
            }));
        } catch(Exception ex) {
            log.warn("Что-тo пошло не так! Сервис упал! {}", ex.getMessage());
        } finally {
            if (producer != null) {
                producer.flush();
                producer.close();
            }
        }

    }
}

