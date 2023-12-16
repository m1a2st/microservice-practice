package tw.com.micro.transformer;

import org.springframework.stereotype.Component;
import tw.com.micro.avro.model.TwitterAvroModel;
import twitter4j.Status;

@Component
public class TwitterStatusToArvoTransformer {

    public TwitterAvroModel getTwitterAvroModel(Status status) {
        return TwitterAvroModel.newBuilder()
                .setId(status.getId())
                .setUserId(status.getUser().getId())
                .setText(status.getText())
                .setCreatedAt(status.getCreatedAt().getTime())
                .build();
    }
}
