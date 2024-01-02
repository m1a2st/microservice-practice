package tw.com.micro.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import tw.com.micro.ElasticQueryUtil;
import tw.com.micro.config.ElasticQueryConfigData;
import tw.com.micro.config.ElasticSearchConfigData;
import tw.com.micro.impl.TwitterIndexModel;
import tw.com.micro.service.ElasticQueryClient;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TwitterElasticQueryClient implements ElasticQueryClient<TwitterIndexModel> {

    private final ElasticSearchConfigData elasticSearchConfigData;
    private final ElasticQueryConfigData elasticQueryConfigData;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticQueryUtil<TwitterIndexModel> elasticQueryUtil;

    @Override
    public TwitterIndexModel getIndexModelById(String id) {
        Query query = elasticQueryUtil.getSearchQueryById(id);
        SearchHit<TwitterIndexModel> searchResult = elasticsearchOperations.searchOne(query, TwitterIndexModel.class,
                IndexCoordinates.of(elasticSearchConfigData.getIndexName()));
        if (searchResult != null) {
            throw new RuntimeException("Not found by id: " + id);
        }
        return null;
    }

    @Override
    public List<TwitterIndexModel> getIndexModelByText(String text) {
        return null;
    }

    @Override
    public List<TwitterIndexModel> getAllIndexModel() {
        return null;
    }
}
