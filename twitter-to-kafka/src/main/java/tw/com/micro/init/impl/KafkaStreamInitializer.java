package tw.com.micro.init.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tw.com.micro.client.KafkaAdminClient;
import tw.com.micro.config.KafkaConfigData;
import tw.com.micro.init.StreamInitializer;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaStreamInitializer implements StreamInitializer {

    private final KafkaConfigData kafkaConfigData;
    private final KafkaAdminClient kafkaAdminClient;

    @Override
    public void init() {
        kafkaAdminClient.createTopics();
        kafkaAdminClient.checkSchemaRegistry();
        log.info("Topics with name {} is ready for operations", kafkaConfigData.getTopicNamesToCreate().toArray());
    }
}
