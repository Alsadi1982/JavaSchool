package sbp.school.kafka.entity;

import sbp.school.kafka.utils.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionEntity {
    private final int id = (int) (Math.random() * Integer.MAX_VALUE);
    private OperationType operationType;
    private BigDecimal sum;
    private long accountNum;
    private String dateOfTransaction;

    public TransactionEntity(OperationType operationType, BigDecimal sum, long accountNum) {
        this.operationType = operationType;
        this.sum = sum;
        this.accountNum = accountNum;
        this.dateOfTransaction = getDateOfTransaction();
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public long getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(long accountNum) {
        this.accountNum = accountNum;
    }

    public String getDateOfTransaction() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }

    public int getId() {
        return id;
    }
}

