package sbp.school.kafka.utils.dao;

import sbp.school.kafka.entity.TransactionEntity;
import sbp.school.kafka.service.TransactionConsumerService;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsumerDao {
    private static final Logger LOGGER = Logger.getLogger(ConsumerDao.class.getName());

    public static final String DATABASE_URL = "jdbc:h2:tcp://localhost/~/test";

    public void saveTransactionInDB (TransactionEntity transaction) {
        try (Connection connect = DriverManager.getConnection(DATABASE_URL, "admin", "")) {
            String query = "INSERT INTO INPUT_TRANSACTIONS (id, operationType, sum, accountNum, dateOfTransaction)" +
                    "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connect.prepareStatement(query)) {
                statement.setInt(1, transaction.getId());
                statement.setString(2, transaction.getOperationType().toString());
                statement.setBigDecimal(3, transaction.getSum());
                statement.setLong(4, transaction.getAccountNum());
                statement.setTimestamp(5, Timestamp.valueOf(transaction.getDateOfTransaction()));
                LOGGER.info("" + statement.executeUpdate());
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING,"Problem with statement!");
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Problem with connection!");
            throw new RuntimeException(ex);
        }
    }
}
