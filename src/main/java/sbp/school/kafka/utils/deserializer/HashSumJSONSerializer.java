package sbp.school.kafka.utils.deserializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sbp.school.kafka.entity.HashSumDto;
import sbp.school.kafka.utils.validation.ValidationUtil;

import java.nio.charset.StandardCharsets;

public class HashSumJSONSerializer implements Serializer<HashSumDto> {

    private static final Logger log = LoggerFactory.getLogger(HashSumJSONSerializer.class);

    @Override
    public byte[] serialize(String topic, HashSumDto data) {
        if (data != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                String value = mapper.writeValueAsString(data);
                ValidationUtil.validateWithJSONSchema(value, "src/main/resources/validator/transactionSchema.json");
               log.info("Success serialization");
                return value.getBytes(StandardCharsets.UTF_8);
            } catch (JsonProcessingException e) {
                log.warn("Serialization fail!!! {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
        log.warn("Object of serialization = null !!!");
        return new byte[0];
    }
}
