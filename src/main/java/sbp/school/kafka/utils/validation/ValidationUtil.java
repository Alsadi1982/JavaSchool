package sbp.school.kafka.utils.validation;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sbp.school.kafka.utils.dao.BackFlowProducerDao;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Утилитный класс содержит методы валидации значений
 */
public class ValidationUtil {

    private static final Logger log = LoggerFactory.getLogger(ValidationUtil.class);

    /**
     * Метод ValidationUtil#validateWithJSONSchema(String value) валидирует JSON схемой
     * @param value - JSON-string
     * @param pathToJSONSchema - путь до JSON-schema
     */
    public static void validateWithJSONSchema(String value, String pathToJSONSchema) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonData = mapper.readTree(value);
            JsonNode jsonSchema = mapper.readTree(new File(pathToJSONSchema));
            JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
            JsonSchema schema = factory.getJsonSchema(jsonSchema);
            ProcessingReport report = schema.validate(jsonData);
            if (report.isSuccess()) {
                log.info("JSON is valid!");
            } else {
                log.warn("JSON is invalid: {}",report);
            }
        } catch (ProcessingException | IOException e){
            log.warn("Problem with validation process!!! {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
