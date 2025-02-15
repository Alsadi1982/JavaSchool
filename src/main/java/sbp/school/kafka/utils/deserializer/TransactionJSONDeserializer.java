package sbp.school.kafka.utils.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import sbp.school.kafka.entity.TransactionEntity;
import sbp.school.kafka.utils.validation.ValidationUtil;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс валидирет полученное сообщение и десириализует его в случае успешной валидации,
 * иначе выкидывает SerializationException
 */
public class TransactionJSONDeserializer implements Deserializer {

    private static final Logger LOGGER = Logger.getLogger(TransactionJSONDeserializer.class.getName());

    @Override
    public Object deserialize(String topic, byte[] data) {
        if (data != null) {
            LOGGER.warning("Value is null");
            throw new SerializationException();
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            String valueStr = mapper.writeValueAsString(data);
            ValidationUtil.validateWithJSONSchema(valueStr, "src/main/resources/validator/transactionSchema.json");
            TransactionEntity transaction = mapper.readValue(data, TransactionEntity.class);
            LOGGER.info("Success deserialization");
            return transaction;
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Deserialization fail!!!", ex);
            throw new SerializationException(ex.getMessage(), ex);
        }
    }
}
