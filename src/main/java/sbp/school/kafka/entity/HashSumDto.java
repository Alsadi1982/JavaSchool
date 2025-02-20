package sbp.school.kafka.entity;

import java.sql.Timestamp;

public class HashSumDto {

    private long hashSum ;
    private Timestamp fromDate;

    public HashSumDto() {
    }

    public HashSumDto(long hashSum, Timestamp fromDate) {
        this.hashSum = hashSum;
        this.fromDate = fromDate;
    }

    public long getHashSum() {
        return hashSum;
    }

    public Timestamp getFromDate() {
        return fromDate;
    }

}
