package tw.com.micro.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tw.com.micro.avro.model.TwitterAvroModel;
import tw.com.micro.config.KafkaConfigData;
import tw.com.micro.service.KafkaProducer;
import tw.com.micro.transformer.TwitterStatusToArvoTransformer;
import twitter4j.Status;
import twitter4j.StatusAdapter;

@Slf4j
@Component
@RequiredArgsConstructor
public class TwitterKafkaStatusListener extends StatusAdapter {

    private final KafkaConfigData kafkaConfigData;
    private final KafkaProducer<Long, TwitterAvroModel> kafkaProducer;
    private final TwitterStatusToArvoTransformer twitterStatusToArvoTransformer;

    @Override
    public void onStatus(Status status) {
        log.info("Twitter status with text {}", status.getText());
        TwitterAvroModel twitterAvroModel = twitterStatusToArvoTransformer.getTwitterAvroModel(status);
        // Kafka partition key: set the target partition for a message
        kafkaProducer.send(kafkaConfigData.getTopicName(), twitterAvroModel.getUserId(), twitterAvroModel);
    }
}
