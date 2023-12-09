package tw.com.micro;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tw.com.micro.config.TwitterToKafkaConfigData;
import tw.com.micro.runners.StreamRunner;
import twitter4j.TwitterException;

@Slf4j
@RequiredArgsConstructor
@SpringBootApplication(scanBasePackages = "tw.com.micro")
public class TwitterToKafkaServiceApplication implements CommandLineRunner {

    private final TwitterToKafkaConfigData twitterToKafkaConfigData;
    private final StreamRunner streamRunner;

    public static void main(String[] args) {
        SpringApplication.run(TwitterToKafkaServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws TwitterException {
        log.info("Welcome message: {}", twitterToKafkaConfigData.getWelcomeMessage());
        log.info("App starts with config: {}", twitterToKafkaConfigData.getTwitterKeywords());
        streamRunner.start();
    }
}
