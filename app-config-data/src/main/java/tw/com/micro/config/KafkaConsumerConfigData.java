package tw.com.micro.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-consumer-config")
public class KafkaConsumerConfigData {

    private String keyDeserializer;
    private String valueDeserializer;
    private String consumerGroupId;
    private String autoOffsetReset;
    private String specificAvroReaderKey;
    private String specificAvroReader;
    private Boolean batchListener;
    private Boolean autoStartup;
    private int concurrencyLevel;
    private int sessionTimeoutMs;
    private int heartbeatIntervalMs;
    private int maxPollIntervalMs;
    private int maxPollRecords;
    private int maxPartitionFetchBytesDefault;
    private int maxPartitionFetchBytesBoostFactor;
    private int pollTimeoutMs;
}
