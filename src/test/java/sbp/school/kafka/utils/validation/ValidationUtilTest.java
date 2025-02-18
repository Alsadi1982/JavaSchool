package sbp.school.kafka.utils.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ValidationUtilTest {

    /**
     * Успешный сценарий валидации json в методе ValidationUtil#validateWithJSONSchema(String value, String pathToJSONSchema)
     */
    @Test
    public void validateWithJSONSchemaTest_Success() {
        String stringifyEntity = "{\"id\":1634996688,\"operationType\":\"DEPOSITING\",\"sum\":12345,\"accountNum\":789456342, \"dateOfTransaction\": \"2025-02-18 23:26:40.202438\"}";
        String pathToFile = "src/main/resources/validator/transactionSchema.json";
        Assertions.assertTrue(ValidationUtil.validateWithJSONSchema(stringifyEntity, pathToFile));
    }

    /**
     * Негативный сценарий Валидации json в методе ValidationUtil#validateWithJSONSchema(String value, String pathToJSONSchema)
     */
    @Test
    public void validateWithJSONSchemaTest_ValidationError() {
        Assertions.assertThrows(RuntimeException.class,
                () -> ValidationUtil.validateWithJSONSchema("value", "src/main/resources/validator/transactionSchema.json"));
    }

}
