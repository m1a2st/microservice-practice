package tw.com.micro;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;

@Configuration
public class KafkaConfig {

    @Bean
    public KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry(){
        return new KafkaListenerEndpointRegistry();
    }
}
