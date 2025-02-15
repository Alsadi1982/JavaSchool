package sbp.school.kafka.utils.validation;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Утилитный класс содержит методы валидации значений
 */
public class ValidationUtil {

    private static final Logger LOGGER = Logger.getLogger(ValidationUtil.class.getName());

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
                LOGGER.info("JSON is valid!");
            } else {
                LOGGER.warning("JSON is invalid: " + report);
            }
        } catch (ProcessingException | IOException e){
            LOGGER.log(Level.WARNING, "Problem with validation process!!!", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
