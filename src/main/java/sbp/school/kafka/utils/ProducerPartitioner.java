package sbp.school.kafka.utils;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Класс отвечает за кастомное распределение сообщений по партициям
 */
public class ProducerPartitioner implements Partitioner {

    private static final Logger LOGGER = Logger.getLogger(ProducerPartitioner.class.getName());

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        List<PartitionInfo> partitionInfos = cluster.partitionsForTopic(topic);
        int numPartitions = partitionInfos.size();
        if (keyBytes == null || !(key instanceof String)) {
            LOGGER.warning("Тип ключа не String");
            throw new IllegalArgumentException();
        }
        if (OperationType.DEPOSITING.name().equals(key)) {
            return numPartitions - 1;
        }

        return 0;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
