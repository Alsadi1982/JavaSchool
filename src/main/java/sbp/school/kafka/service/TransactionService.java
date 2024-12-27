package sbp.school.kafka.service;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import sbp.school.kafka.entity.TransactionEntity;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс реализует основную логику отправки сообщений
 */
public class TransactionService {

    private static final Logger LOGGER = Logger.getLogger(TransactionService.class.getName());
    private final Properties props;

    public TransactionService(Properties props) {
        this.props = props;
    }

    /**
     * Метод TransactionService#send(ProducerRecord<String, TransactionEntity> record) отправка сообщения
     * @param record
     */
    public void send(ProducerRecord<String, TransactionEntity> record){
        KafkaProducer<String, TransactionEntity> producer = null;
        try  {
            producer = new KafkaProducer<>(props);
            Future<RecordMetadata> future = producer.send(record, ((metadata, exception) -> {
                if (exception != null) {
                    String errorMessage = String.format("Сбой передачи сообщения! offset = %d, partition = %d, Exception: %s",
                            metadata.offset(), metadata.partition(), exception.getMessage());
                    LOGGER.log(Level.WARNING, errorMessage, exception);
                } else {
                    String successMessage = String.format("Успешная отправка сообщения! offset = %d, partition = %d, topic = %s",
                            metadata.offset(), metadata.partition(), metadata.topic());
                    LOGGER.info(successMessage);
                }
            }));
        } catch(Exception ex) {
            LOGGER.log(Level.WARNING, "Что-тo пошло не так! Сервис упал!", ex.getMessage());
        } finally {
            if (producer != null) {
                producer.flush();
                producer.close();
            }
        }

    }
}
