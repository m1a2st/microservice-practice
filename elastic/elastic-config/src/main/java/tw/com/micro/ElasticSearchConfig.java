package tw.com.micro;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import tw.com.micro.config.ElasticSearchConfigData;

import java.util.Objects;

@Configuration
public class ElasticSearchConfig extends AbstractElasticsearchConfiguration {

    private final ElasticSearchConfigData elasticSearchConfigData;

    public ElasticSearchConfig(ElasticSearchConfigData elasticSearchConfigData) {
        this.elasticSearchConfigData = elasticSearchConfigData;
    }

    @Bean
    @Override
    public RestHighLevelClient elasticsearchClient() {
        UriComponents serverUri = UriComponentsBuilder
                .fromHttpUrl(elasticSearchConfigData.getConnectionUrl()).build();
        return new RestHighLevelClient(RestClient.builder(
                new HttpHost(
                        Objects.requireNonNull(serverUri.getHost()),
                        serverUri.getPort(),
                        serverUri.getScheme()
                )).setHttpClientConfigCallback(
                requestConfigBuilder -> requestConfigBuilder
                        .setMaxConnTotal(elasticSearchConfigData.getConnectionTimeoutMs())
                        .setMaxConnPerRoute(elasticSearchConfigData.getSocketTimeoutMs())
        ));
    }
}
