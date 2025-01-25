package sbp.school.kafka.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sbp.school.kafka.entity.TransactionEntity;
import sbp.school.kafka.utils.OperationType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BackFlowJDBCService {
    private static final Logger log = LoggerFactory.getLogger(BackFlowJDBCService.class);

    public static final String DATABASE_URL = "jdbc:h2:tcp://localhost/~/test";

    public List<TransactionEntity> getListByPeriod(Timestamp fromDate) {
        List<TransactionEntity> transactionList = new ArrayList<>();
        try (Connection connect = DriverManager.getConnection(DATABASE_URL, "admin", "")) {
            String query = "SELECT * FROM input_transactions WHERE dateOfTransaction"  +
                    "BETWEEN cast (? as timestamp) AND cast (? as timestamp) - cast (? as interval minute)";
            try (PreparedStatement statement = connect.prepareStatement(query)) {
                statement.setTimestamp(1, fromDate);
                statement.setInt(2, 10);
                if (statement.execute()) {
                    ResultSet resultSet = statement.getResultSet();
                    while (resultSet.next()) {
                        TransactionEntity transactionEntity = new TransactionEntity();
                        transactionEntity.setId(resultSet.getInt("id"));
                        transactionEntity.setSum(resultSet.getBigDecimal("sum"));
                        transactionEntity.setOperationType(OperationType.valueOf(resultSet.getString("operationType")));
                        transactionEntity.setAccountNum(resultSet.getLong("accountNum"));
                        transactionEntity.setDateOfTransaction(resultSet.getTimestamp("dateOfTransaction").toString());
                        transactionList.add(transactionEntity);
                    }
                    log.info("Get list of transactions was success: {}", transactionList);
                }
            } catch (SQLException ex) {
                log.error("Problem with statement!");
            }
        } catch (SQLException ex) {
            log.error("Problem with connection!");
            throw new RuntimeException(ex);
        }
        return transactionList;
    }
}
