package tw.com.micro.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import tw.com.micro.impl.TwitterIndexModel;
import tw.com.micro.repository.TwitterElasticsearchIndexRepository;
import tw.com.micro.service.ElasticClient;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "elastic-config.is-repository", havingValue = "true", matchIfMissing = true)
public class TwitterElasticRepositoryIndexClient implements ElasticClient<TwitterIndexModel> {

    private final TwitterElasticsearchIndexRepository repository;

    @Override
    public List<String> save(List<TwitterIndexModel> documents) {
        List<TwitterIndexModel> response = (List<TwitterIndexModel>) repository.saveAll(documents);
        return response.stream()
                .map(TwitterIndexModel::getId)
                .peek(id -> {
                    log.info("Indexed document with type: {} and id: {}",
                            TwitterIndexModel.class.getName(),
                            id);
                })
                .collect(Collectors.toList());
    }
}

