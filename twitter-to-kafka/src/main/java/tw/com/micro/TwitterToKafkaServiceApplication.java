package tw.com.micro;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tw.com.micro.init.StreamInitializer;
import tw.com.micro.runners.StreamRunner;
import twitter4j.TwitterException;

@Slf4j
@RequiredArgsConstructor
@SpringBootApplication(scanBasePackages = "tw.com.micro")
public class TwitterToKafkaServiceApplication implements CommandLineRunner {

    private final StreamRunner streamRunner;
    private final StreamInitializer streamInitializer;

    public static void main(String[] args) {
        SpringApplication.run(TwitterToKafkaServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws TwitterException {
        log.info("App starts...");
        streamInitializer.init();
        streamRunner.start();
    }
}
