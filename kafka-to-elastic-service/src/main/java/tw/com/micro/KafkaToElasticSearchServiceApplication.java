package tw.com.micro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "tw.com.micro")
public class KafkaToElasticSearchServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaToElasticSearchServiceApplication.class, args);
    }
}
