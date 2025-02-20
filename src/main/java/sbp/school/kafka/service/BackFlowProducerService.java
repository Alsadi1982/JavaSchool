package sbp.school.kafka.service;

import org.apache.kafka.clients.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sbp.school.kafka.entity.HashSumDto;

import java.util.Properties;
import java.util.concurrent.Future;
import java.util.logging.Level;


public class BackFlowProducerService {

    private static final Logger log = LoggerFactory.getLogger(BackFlowProducerService.class);
    private final Properties props;
    private Producer<String, HashSumDto> producer = null;
    private Exception exception;


    public BackFlowProducerService(Properties props, Producer<String, HashSumDto> producer) {
        this.props = props;
        this.producer = producer;
    }

    public Future<RecordMetadata> send(ProducerRecord<String, HashSumDto> record){
        try  {
            Future<RecordMetadata> future = producer.send(record, producerCallbackFunction());
            return future;
        } catch(Exception ex) {
            exception = ex;
            log.error("Что-тo пошло не так! Сервис упал! {}", ex.getMessage());
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            if (producer != null && exception != null) {
                producer.flush();
                producer.close();
            }
        }

    }

    private Callback producerCallbackFunction() {
        return (metadata, exception) -> {
            if (exception != null) {
                String errorMessage = String.format("Сбой передачи сообщения! offset = %d, partition = %d, Exception: %s",
                        metadata.offset(), metadata.partition(), exception.getMessage());
                log.error(errorMessage, exception);

            } else {
                String successMessage = String.format("Успешная отправка сообщения! offset = %d, partition = %d, topic = %s",
                        metadata.offset(), metadata.partition(), metadata.topic());
                log.info(successMessage);
            }
        };
    }
}

