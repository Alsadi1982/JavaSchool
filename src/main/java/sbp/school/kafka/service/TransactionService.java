package sbp.school.kafka.service;

import org.apache.kafka.clients.producer.*;
import sbp.school.kafka.entity.TransactionEntity;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс реализует основную логику отправки сообщений
 */
public class TransactionService {

    private static final Logger LOGGER = Logger.getLogger(TransactionService.class.getName());
    private final Properties props;
    private Producer<String, TransactionEntity> producer = null;
    private Exception exception;


    public TransactionService(Properties props, Producer<String, TransactionEntity> producer) {
        this.props = props;
        this.producer = producer;
    }

    /**
     * Метод TransactionService#send(ProducerRecord<String, TransactionEntity> record) отправка сообщения
     * @param record
     */
    public Future<RecordMetadata> send(ProducerRecord<String, TransactionEntity> record){
        try  {
            Future<RecordMetadata> future = producer.send(record, producerCallbackFunction());
            return future;
        } catch(Exception ex) {
            LOGGER.log(Level.SEVERE, "Что-тo пошло не так! Сервис упал!", ex.getMessage());
            exception = ex;
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            if (producer != null && exception != null) {
                producer.flush();
                producer.close();
            }
        }
    }

    /**
     * Метод TransactionService#reSend(TransactionEntity transaction, String kafkaTopic) переотправка сообщений
     * из БД в случае если после проверки обратного потока из консюмера не сошлась хэш-сумма
     * @param transaction
     * @param kafkaTopic
     */
    public void reSend(TransactionEntity transaction, String kafkaTopic){
        ProducerRecord<String, TransactionEntity> record = new ProducerRecord<>(kafkaTopic, transaction.getOperationType().name(), transaction);
        try  {
            Future<RecordMetadata> future = producer.send(record, ((metadata, exception) -> {
                if (exception != null) {
                    String errorMessage = String.format("Сбой передачи сообщения! offset = %d, partition = %d, Exception: %s",
                            metadata.offset(), metadata.partition(), exception.getMessage());
                    LOGGER.log(Level.WARNING, errorMessage, exception);

                } else {
                    String successMessage = String.format("Успешная отправка сообщения! offset = %d, partition = %d, topic = %s",
                            metadata.offset(), metadata.partition(), metadata.topic());
                    LOGGER.info(successMessage);
                    JDBSService jdbsService = new JDBSService();
                    jdbsService.saveTransactionInDB(transaction);
                }
            }));
        } catch(Exception ex) {
            LOGGER.log(Level.SEVERE, "Что-тo пошло не так! Сервис упал!", ex.getMessage());
        } finally {
            if (producer != null) {
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
                LOGGER.log(Level.WARNING, errorMessage, exception);

            } else {
                String successMessage = String.format("Успешная отправка сообщения! offset = %d, partition = %d, topic = %s",
                        metadata.offset(), metadata.partition(), metadata.topic());
                LOGGER.info(successMessage);
            }
        };
    }
}
