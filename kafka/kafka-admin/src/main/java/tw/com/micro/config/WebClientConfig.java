package tw.com.micro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebClientConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

//    @Bean
//    public WebClient webClient() {
//        return WebClient.builder()
//                .clientConnector(new ReactorClientHttpConnector())
//                .build();
//    }
}
