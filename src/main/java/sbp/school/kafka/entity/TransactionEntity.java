package sbp.school.kafka.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import sbp.school.kafka.utils.OperationType;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class TransactionEntity {
    private int id = (int) (Math.random() * Integer.MAX_VALUE);
    @JsonProperty("operationType")
    private OperationType operationType;
    @JsonProperty("sum")
    private BigDecimal sum;
    @JsonProperty("accountNum")
    private long accountNum;
    private String dateOfTransaction;

    public TransactionEntity() {
    }

    public TransactionEntity(OperationType operationType, BigDecimal sum, long accountNum) {
        this.operationType = operationType;
        this.sum = sum;
        this.accountNum = accountNum;
        this.dateOfTransaction = getPresentTime();
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
        return dateOfTransaction;
    }

    public String getPresentTime() {
        return Timestamp.valueOf(LocalDateTime.now()).toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDateOfTransaction(String dateOfTransaction) {
        this.dateOfTransaction = dateOfTransaction;
    }

    @Override
    public String toString() {
        return "TransactionEntity{" +
                "id=" + id +
                ", operationType=" + operationType +
                ", sum=" + sum +
                ", accountNum=" + accountNum +
                ", dateOfTransaction='" + dateOfTransaction + '\'' +
                '}';
    }
}

