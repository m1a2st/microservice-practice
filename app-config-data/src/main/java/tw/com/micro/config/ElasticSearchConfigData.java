package tw.com.micro.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "elastic-config")
public class ElasticSearchConfigData {

    private String indexName;
    private String connectionUrl;
    private Integer connectionTimeoutMs;
    private Integer socketTimeoutMs;
}