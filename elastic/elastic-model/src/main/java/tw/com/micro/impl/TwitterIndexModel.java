package tw.com.micro.impl;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import tw.com.micro.IndexModel;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static org.springframework.data.elasticsearch.annotations.FieldType.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "#{elasticConfig.indexName}")
public class TwitterIndexModel implements IndexModel {

    @JsonProperty
    private String id;
    @JsonProperty
    private Long userId;
    @JsonProperty
    private String text;

    @Field(type = Date, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSS'Z'")
    @JsonProperty
    @JsonFormat(shape = STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;
}
