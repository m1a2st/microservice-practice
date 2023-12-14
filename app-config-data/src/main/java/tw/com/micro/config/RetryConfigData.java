package tw.com.micro.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "retry-config")
public class RetryConfigData {

    // 初始間隔時間
    private Long initialIntervalMs;
    // 最大間隔時間
    private Long maxIntervalMs;
    // 乘數
    private Double multiplier;
    // 最大重試次數
    private Integer maxAttempts;
    // 睡眠時間
    private Long sleepTimeMs;
}
