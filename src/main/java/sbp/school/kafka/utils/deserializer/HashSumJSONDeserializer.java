package sbp.school.kafka.utils.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sbp.school.kafka.entity.HashSumDto;
import sbp.school.kafka.utils.validation.ValidationUtil;

import java.io.IOException;

public class HashSumJSONDeserializer implements Deserializer<HashSumDto> {

    private static final Logger log = LoggerFactory.getLogger(HashSumJSONDeserializer.class);

    @Override
    public HashSumDto deserialize(String topic, byte[] data) {
        if (data != null) {
            log.warn("Value is null");
            throw new SerializationException();
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            String valueStr = mapper.writeValueAsString(data);
            ValidationUtil.validateWithJSONSchema(valueStr, "src/main/resources/validator/hashSumSchema.json");
            HashSumDto hashSumDto = mapper.readValue(data, HashSumDto.class);
           log.info("Success deserialization");
            return hashSumDto;
        } catch (IOException ex) {
            log.warn("Deserialization fail!!!", ex);
            throw new SerializationException(ex.getMessage(), ex);
        }
    }
}
