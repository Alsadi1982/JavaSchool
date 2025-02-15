package sbp.school.kafka.utils.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import sbp.school.kafka.entity.TransactionEntity;
import sbp.school.kafka.utils.validation.ValidationUtil;


import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс отвечающий за сериализацию объекта TransactionEntity в JSON
 */
public class TransactionJSONSerializer implements Serializer<TransactionEntity> {

    private static final Logger LOGGER = Logger.getLogger(TransactionJSONSerializer.class.getName());

    @Override
    public byte[] serialize(String topic, TransactionEntity data) {
        if (data != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                String value = mapper.writeValueAsString(data);
                ValidationUtil.validateWithJSONSchema(value, "src/main/resources/validator/transactionSchema.json");
                LOGGER.info("Success serialization");
                return value.getBytes(StandardCharsets.UTF_8);
            } catch (JsonProcessingException e) {
                LOGGER.log(Level.WARNING, "Serialization fail!!!", e.getMessage());
                throw new RuntimeException(e);
            }
        }
        LOGGER.warning("Object of serialization = null !!!");
        return new byte[0];
    }
}
