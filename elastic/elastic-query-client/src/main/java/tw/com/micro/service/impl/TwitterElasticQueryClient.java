package tw.com.micro.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import tw.com.micro.ElasticQueryUtil;
import tw.com.micro.config.ElasticQueryConfigData;
import tw.com.micro.config.ElasticSearchConfigData;
import tw.com.micro.exceptions.ElasticQueryClientException;
import tw.com.micro.impl.TwitterIndexModel;
import tw.com.micro.service.ElasticQueryClient;

import java.util.List;
import java.util.stream.Collectors;

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
        if (searchResult == null) {
            log.error("Not found by id: {}", id);
            throw new ElasticQueryClientException("Not found by id: " + id);
        }
        log.info("Get index model by id: {}", id);
        return searchResult.getContent();
    }

    @Override
    public List<TwitterIndexModel> getIndexModelByText(String text) {
        Query query = elasticQueryUtil.getSearchQueryByFieldText(elasticQueryConfigData.getTextField(), text);
        SearchHits<TwitterIndexModel> searchResult = elasticsearchOperations.search(query, TwitterIndexModel.class,
                IndexCoordinates.of(elasticSearchConfigData.getIndexName()));
        log.info("Get index model by text: {}", text);
        return searchResult.getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    @Override
    public List<TwitterIndexModel> getAllIndexModel() {
        Query query = elasticQueryUtil.getSearchQueryForAll();
        SearchHits<TwitterIndexModel> searchResult = elasticsearchOperations.search(query, TwitterIndexModel.class,
                IndexCoordinates.of(elasticSearchConfigData.getIndexName()));
        log.info("Get all index model");
        return searchResult.getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
}
