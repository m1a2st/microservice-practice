package tw.com.micro.client;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientResponse;
import tw.com.micro.config.KafkaConfigData;
import tw.com.micro.config.RetryConfigData;
import tw.com.micro.exceptions.KafkaClientException;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;


@Component
public class KafkaAdminClient {

    private static final Logger logger = LoggerFactory.getLogger(KafkaAdminClient.class);
    private final KafkaConfigData kafkaConfigData;
    private final RetryConfigData retryConfigData;
    private final AdminClient adminClient;
    private final RetryTemplate retryTemplate;
    private final RestTemplate restTemplate;

    public KafkaAdminClient(KafkaConfigData kafkaConfigData, RetryConfigData retryConfigData, AdminClient adminClient, RetryTemplate retryTemplate, RestTemplate restTemplate) {
        this.kafkaConfigData = kafkaConfigData;
        this.retryConfigData = retryConfigData;
        this.adminClient = adminClient;
        this.retryTemplate = retryTemplate;
        this.restTemplate = restTemplate;
    }

    public void createTopics() {
        CreateTopicsResult createTopicResult;
        try {
            createTopicResult = retryTemplate.execute(this::doCreateTopic);
        } catch (RuntimeException e) {
            throw new KafkaClientException("Reached maximum number of retries for topic creation", e);
        }
        checkTopicsCreated();
    }

    public void checkSchemaRegistry() {
        logger.info("Checking Schema Registry");
        int retryCount = 1;
        Integer maxAttempts = retryConfigData.getMaxAttempts();
        int multiplier = retryConfigData.getMultiplier().intValue();
        Long sleepTimeMs = retryConfigData.getSleepTimeMs();
        while (!getSchemaRegistryStatus().is2xxSuccessful()) {
            checkMaxRetry(retryCount++, maxAttempts);
            sleep(sleepTimeMs);
            sleepTimeMs *= multiplier;
        }
    }

    private HttpStatus getSchemaRegistryStatus() {
        try {
            return restTemplate
                    .exchange(kafkaConfigData.getSchemaRegistryUrl(),
                            GET,
                            null,
                            ClientResponse.class)
                    .getStatusCode();
        } catch (Exception e) {
            return SERVICE_UNAVAILABLE;
        }
    }

    private CreateTopicsResult doCreateTopic(RetryContext retryContext) {
        List<String> topicNames = kafkaConfigData.getTopicNamesToCreate();
        logger.info("Creating {} topics(s)", topicNames.size());
        List<NewTopic> kafkaTopics = topicNames.stream()
                .map(topic -> new NewTopic(
                        topic.trim(),
                        kafkaConfigData.getNumOfPartitions(),
                        kafkaConfigData.getReplicationFactor()))
                .collect(Collectors.toList());
        return adminClient.createTopics(kafkaTopics);
    }

    private void checkTopicsCreated() {
        Collection<TopicListing> topics = getTopics();
        int retryCount = 1;
        Integer maxAttempts = retryConfigData.getMaxAttempts();
        int multiplier = retryConfigData.getMultiplier().intValue();
        Long sleepTimeMs = retryConfigData.getSleepTimeMs();
        for (String topic : kafkaConfigData.getTopicNamesToCreate()) {
            while (!isTopicCreated(topics, topic)) {
                checkMaxRetry(retryCount++, maxAttempts);
                sleep(sleepTimeMs);
                sleepTimeMs *= multiplier;
                topics = getTopics();
            }
        }
    }

    private boolean isTopicCreated(Collection<TopicListing> topics, String topic) {
        if (topics == null) {
            return false;
        }
        return topics.stream().anyMatch(topicListing -> topicListing.name().equals(topic));
    }

    private void checkMaxRetry(int i, Integer maxAttempts) {
        if (i > maxAttempts) {
            throw new KafkaClientException("Could not verify if topics are created");
        }
    }

    private void sleep(Long sleepTimeMs) {
        try {
            Thread.sleep(sleepTimeMs);
        } catch (InterruptedException e) {
            throw new KafkaClientException("Sleep interrupted while waiting for topic creation", e);
        }
    }

    private Collection<TopicListing> getTopics() {
        Collection<TopicListing> topics;
        try {
            topics = retryTemplate.execute(this::doGetTopics);
        } catch (Exception e) {
            throw new KafkaClientException("Reached maximum number of retries for listing topics", e);
        }
        return topics;
    }

    private Collection<TopicListing> doGetTopics(RetryContext retryContext) throws ExecutionException, InterruptedException {
        logger.info("Reading Kafka topics");
        Collection<TopicListing> topics = adminClient.listTopics().listings().get();
        if (topics != null) {
            topics.forEach(topicListing -> logger.info("Topic: {}", topicListing.name()));
        }
        return topics;
    }
}
