package sbp.school.kafka.entity;

import java.sql.Timestamp;

public class HashSumDto {

    private final long hashSum;
    private final Timestamp fromDate;

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
