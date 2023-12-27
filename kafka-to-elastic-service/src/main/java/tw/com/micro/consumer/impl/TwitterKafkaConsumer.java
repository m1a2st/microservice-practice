package tw.com.micro.consumer.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import tw.com.micro.avro.model.TwitterAvroModel;
import tw.com.micro.client.KafkaAdminClient;
import tw.com.micro.config.KafkaConfigData;
import tw.com.micro.config.KafkaConsumerConfigData;
import tw.com.micro.consumer.KafkaConsumer;
import tw.com.micro.impl.TwitterIndexModel;
import tw.com.micro.service.ElasticClient;
import tw.com.micro.service.impl.TwitterElasticIndexClient;
import tw.com.micro.transformers.AvroToElasticModelTransformer;

import java.util.List;
import java.util.Objects;

import static org.springframework.kafka.support.KafkaHeaders.*;

@Slf4j
@Service
public class TwitterKafkaConsumer implements KafkaConsumer<Long, TwitterAvroModel> {

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    private final KafkaAdminClient kafkaAdminClient;
    private final KafkaConfigData kafkaConfigData;
    private final KafkaConsumerConfigData kafkaConsumerConfigData;
    private final AvroToElasticModelTransformer transformer;
    private final ElasticClient<TwitterIndexModel> elasticClient;

    public TwitterKafkaConsumer(KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry,
                                KafkaAdminClient kafkaAdminClient,
                                KafkaConfigData kafkaConfigData, KafkaConsumerConfigData kafkaConsumerConfigData,
                                AvroToElasticModelTransformer transformer,
                                ElasticClient<TwitterIndexModel> elasticClient) {
        this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
        this.kafkaAdminClient = kafkaAdminClient;
        this.kafkaConfigData = kafkaConfigData;
        this.kafkaConsumerConfigData = kafkaConsumerConfigData;
        this.transformer = transformer;
        this.elasticClient = elasticClient;
    }

    @EventListener
    public void onAppStartUp(ApplicationStartedEvent event) {
        kafkaAdminClient.checkTopicsCreated();
        log.info("ApplicationStartedEvent: All topics {} are created",
                kafkaConfigData.getTopicNamesToCreate().toArray());
        Objects.requireNonNull(kafkaListenerEndpointRegistry.getListenerContainer(kafkaConsumerConfigData.getConsumerGroupId()))
                .start();
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.consumer-group-id}", topics = "${kafka-config.topic-name}")
    public void receive(@Payload List<TwitterAvroModel> messages,
                        @Header(RECEIVED_KEY) List<Integer> keys,
                        @Header(RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(OFFSET) List<Long> offsets) {
        log.info("{} number of message received with keys {}, partitions {} and offsets {}, " +
                        "sending it to elastic: Thread id {}",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString(),
                Thread.currentThread().getId());
        List<TwitterIndexModel> elasticModels = transformer.getElasticModels(messages);
        List<String> documentIds = elasticClient.save(elasticModels);
        log.info("Documents with ids {} are indexed in elastic", documentIds.toArray());

    }
}
