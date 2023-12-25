package tw.com.micro.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;
import tw.com.micro.config.ElasticSearchConfigData;
import tw.com.micro.impl.TwitterIndexModel;
import tw.com.micro.service.ElasticClient;
import tw.com.micro.utils.ElasticUtil;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TwitterElasticIndexClient implements ElasticClient<TwitterIndexModel> {

    private final ElasticSearchConfigData elasticSearchConfigData;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticUtil<TwitterIndexModel> elasticUtil;

    @Override
    public List<String> save(List<TwitterIndexModel> documents) {
        return elasticsearchOperations.bulkIndex(
                        elasticUtil.getIndexQueryList(documents),
                        IndexCoordinates.of(elasticSearchConfigData.getIndexName()))
                .stream()
                .peek(indexQuery ->
                        log.info("Indexed document with type: {} and id: {}",
                                TwitterIndexModel.class.getName(),
                                indexQuery.getId()))
                .map(IndexedObjectInformation::getId)
                .collect(Collectors.toList());
    }
}
