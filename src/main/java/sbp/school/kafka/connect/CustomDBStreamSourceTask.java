package sbp.school.kafka.connect;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sbp.school.kafka.config.AppConfig;
import sbp.school.kafka.entity.TransactionEntity;
import sbp.school.kafka.utils.OperationType;


import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * DBStreamSourceTask reads from db.
 */
public class CustomDBStreamSourceTask extends SourceTask {

    private static final Logger log = LoggerFactory.getLogger(CustomDBStreamSourceTask.class);
    public static final String DATABASE_NAME_FIELD = "db_name";
    public  static final String POSITION_FIELD = "position";

    private String dbUrlPath;
    private String dbUsername;
    private String dbPassword;
    private String tableName;
    private char[] buffer;
    private Connection connect;
    private long lastModifiedOffset = 0;
    private String topic;
    private int batchSize;

    private Long streamOffset;

    public CustomDBStreamSourceTask() {
        this(Integer.parseInt(AppConfig.getAppProperties().getProperty("initial.buffer.size")));
    }
    CustomDBStreamSourceTask(int initialBufferSize) {
        buffer = new char[initialBufferSize];
    }

    @Override
    public String version() {
        return new CustomDBStreamSourceConnector().version();
    }

    @Override
    public void start(Map<String, String> props) {
        AbstractConfig config = new AbstractConfig(CustomFileStreamSourceConnector.CONFIG_DEF, props);
        dbUrlPath = config.getString(CustomDBStreamSourceConnector.DATABASE_URL);
        dbUsername =  config.getString(CustomDBStreamSourceConnector.DATABASE_USERNAME);
        dbPassword =  config.getString(CustomDBStreamSourceConnector.DATABASE_PASSWORD);
        tableName = config.getString(CustomDBStreamSourceConnector.DATABASE_TABLE);
        topic = config.getString(CustomDBStreamSourceConnector.TOPIC_CONFIG);
        batchSize = config.getInt(CustomDBStreamSourceConnector.TASK_BATCH_SIZE_CONFIG);
        try {
            connect = DriverManager.getConnection(dbUrlPath, dbUsername, dbPassword);
            log.info("Successful connection with DB!");
        }catch (SQLException ex) {
            log.error("Problem with connection!", ex);
            throw  new RuntimeException(ex);
        }
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        List<SourceRecord> records = new ArrayList<>();
        Map<String, Object> offset = context.offsetStorageReader().offset(Collections.singletonMap(DATABASE_NAME_FIELD, tableName));
        String query = "SELECT * FROM ? WHERE dateOfTransaction > ?";
        try(PreparedStatement statement = connect.prepareStatement(query)){
            statement.setString(1, tableName );
            statement.setTimestamp(2, new Timestamp(lastModifiedOffset));
            if (statement.execute()) {
                ResultSet resultSet = statement.getResultSet();
                while (resultSet.next()) {
                    TransactionEntity transactionEntity = new TransactionEntity();
                    transactionEntity.setId(resultSet.getInt("id"));
                    transactionEntity.setSum(resultSet.getBigDecimal("sum"));
                    transactionEntity.setOperationType(OperationType.valueOf(resultSet.getString("operationType")));
                    transactionEntity.setAccountNum(resultSet.getLong("accountNum"));
                    transactionEntity.setDateOfTransaction(resultSet.getTimestamp("dateOfTransaction").toString());
                    long lastModified = resultSet.getTimestamp("dateOfTransaction").getTime();
                    log.info("lastModifiedOffset = {}, lastModified = {}", lastModifiedOffset, lastModified);
                    if (lastModified > lastModifiedOffset) {
                        lastModifiedOffset = lastModified;
                    }

                    SourceRecord record = new SourceRecord(offsetKey(tableName),
                            offsetValue(lastModifiedOffset),
                            topic,
                            null,
                            null,
                            getTransactionDtoSchema(),
                            getTransactionDtoStruct(transactionEntity));
                    log.info("record = {}", record);
                    records.add(record);
                }
            }
        }catch (SQLException ex) {
            log.error("Problem with statement!", ex);
            throw new RuntimeException(ex.getMessage());
        }
        return records;
    }

    private Map<String, String> offsetKey(String tableName) {
        return Collections.singletonMap(DATABASE_NAME_FIELD, tableName);
    }

    private Map<String, Long> offsetValue(Long pos) {
        return Collections.singletonMap(POSITION_FIELD, pos);
    }

    private Schema getTransactionDtoSchema() {
        return SchemaBuilder.struct()
                .field("id", Schema.INT32_SCHEMA)
                .field("operationType", Schema.STRING_SCHEMA)
                .field("sum", Schema.STRING_SCHEMA)
                .field("accountNum", Schema.INT64_SCHEMA)
                .field("dateOfTransaction", Schema.STRING_SCHEMA)
                .build();
    }

    private Struct getTransactionDtoStruct(TransactionEntity transaction) {
        Struct struct = new Struct(getTransactionDtoSchema());
        struct.put("id", transaction.getId());
        struct.put("operationType", transaction.getOperationType().toString());
        struct.put("sum", transaction.getSum().toString());
        struct.put("accountNum", transaction.getSum());
        struct.put("dateOfTransaction", transaction.getDateOfTransaction());
        return struct;
    }

    @Override
    public void stop() {
        try {
            if (connect != null) {
                connect.close();
            }
        } catch (Exception e) {
            log.error("Ошибка закрытия соединения с H2", e);
            throw new RuntimeException("Ошибка закрытия соединения с H2", e);
        }
    }
}
