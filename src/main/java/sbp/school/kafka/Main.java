package sbp.school.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import sbp.school.kafka.config.KafkaConfig;
import sbp.school.kafka.config.LoggerConfig;
import sbp.school.kafka.entity.TransactionEntity;
import sbp.school.kafka.service.TransactionService;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    static {
        LoggerConfig.getLoggerConfig();
    }

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Пожалуйста укажите путь до файла со списком транзакций и название топика!");
            System.out.println("Пример: java -jar Lecture2.jar /path/to/transactions_list.txt topic_name");
            LOGGER.warning("Не указан путь до файла со списком транзакций или топик!");
        }else {
            String line;
            try (BufferedReader input = new BufferedReader(new FileReader(args[0]))) {
                while((line = input.readLine()) != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    TransactionEntity transaction = mapper.readValue(line, TransactionEntity.class);
                    ProducerRecord<String, TransactionEntity> record = new ProducerRecord<>(args[1], transaction.getOperationType().name(), transaction);
                    TransactionService transactionService = new TransactionService(KafkaConfig.getKafkaProperties());
                    transactionService.send(record);
                }

            } catch(IOException ex) {
                LOGGER.log(Level.WARNING, "File not found!!!", ex.getMessage());
                throw new RuntimeException(ex.getMessage());
            }
        }
    }
}
