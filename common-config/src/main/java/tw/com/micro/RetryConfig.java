package tw.com.micro;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import tw.com.micro.config.RetryConfigData;

@Configuration
public class RetryConfig {

    private final RetryConfigData retryConfigData;

    public RetryConfig(RetryConfigData retryConfigData) {
        this.retryConfigData = retryConfigData;
    }

    @Bean
    public RetryTemplate retryTemplate(){
        RetryTemplate retryTemplate = new RetryTemplate();
        // Increase wait time for each retry attempt
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(retryConfigData.getInitialIntervalMs());
        backOffPolicy.setMaxInterval(retryConfigData.getMaxIntervalMs());
        backOffPolicy.setMultiplier(retryConfigData.getMultiplier());
        retryTemplate.setBackOffPolicy(backOffPolicy);

        // Retry max attempts is reached
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(retryConfigData.getMaxAttempts());

        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }
}
