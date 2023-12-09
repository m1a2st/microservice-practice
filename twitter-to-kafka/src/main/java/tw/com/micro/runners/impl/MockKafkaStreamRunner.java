package tw.com.micro.runners.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import tw.com.micro.config.TwitterToKafkaConfigData;
import tw.com.micro.exceptions.TwitterToKafkaServiceException;
import tw.com.micro.listeners.TwitterKafkaStatusListener;
import tw.com.micro.runners.StreamRunner;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Long.MAX_VALUE;
import static java.time.ZonedDateTime.now;
import static java.util.Locale.ENGLISH;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "twitter-to-kafka-service.enable-mock-tweets", havingValue = "true", matchIfMissing = true)
public class MockKafkaStreamRunner implements StreamRunner {

    private final TwitterToKafkaConfigData twitterToKafkaConfigData;
    private final TwitterKafkaStatusListener twitterKafkaStatusListener;
    private static final Random RANDOM = new Random();
    private static final String[] WORDS = new String[]{
            "Lorem",
            "ipsum",
            "dolor",
            "sit",
            "amet",
            "consectetuer",
            "adipiscing",
            "elit",
            "Maecenas",
            "porttitor",
            "congue",
            "massa",
            "Fusce",
            "posuere",
            "magna",
            "sed",
            "pulvinar",
            "ultricies",
            "purus",
            "lectus",
            "malesuada",
            "libero"
    };

    private static final String tweetAsRawJson = "{" +
            "\"created_at\":\"{0}\"," +
            "\"id\":\"{1}\"," +
            "\"text\":\"{2}\"," +
            "\"user\":{\"id\":\"{3}\"}" +
            "}";

    private static final String TWITTER_STATUS_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";

    @Override
    public void start() {
        final String[] keywords = twitterToKafkaConfigData.getTwitterKeywords().toArray(new String[0]);
        final int minTweetLength = twitterToKafkaConfigData.getMockMinTweetLength();
        final int maxTweetLength = twitterToKafkaConfigData.getMockMaxTweetLength();
        long sleepTimeMs = twitterToKafkaConfigData.getMockSleepMs();
        log.info("Starting mock filtering twitter streams for keywords {}", Arrays.toString(keywords));
        simulateTwitterStream(keywords, minTweetLength, maxTweetLength, sleepTimeMs);
    }

    private void simulateTwitterStream(String[] keywords, int minTweetLength, int maxTweetLength, long sleepTimeMs) {
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                while (true) {
                    String formattedTweetAsRawJson = getFormattedTweet(keywords, minTweetLength, maxTweetLength);
                    Status status = TwitterObjectFactory.createStatus(formattedTweetAsRawJson);
                    twitterKafkaStatusListener.onStatus(status);
                    sleep(sleepTimeMs);
                }
            } catch (TwitterException e) {
                log.error("Error creating twitter status!", e);
            }
        });
    }

    private String getFormattedTweet(String[] keywords, int minTweetLength, int maxTweetLength) {
        String[] params = new String[]{
                now().format(DateTimeFormatter.ofPattern(TWITTER_STATUS_DATE_FORMAT, ENGLISH)),
                String.valueOf(ThreadLocalRandom.current().nextLong(MAX_VALUE)),
                getRandomTweetContent(keywords, minTweetLength, maxTweetLength),
                String.valueOf(ThreadLocalRandom.current().nextLong(MAX_VALUE))
        };
        return formatTweetAsJsonWithParams(params);
    }

    private String getRandomTweetContent(String[] keywords, int minTweetLength, int maxTweetLength) {
        int tweetLength = RANDOM.nextInt(maxTweetLength - minTweetLength + 1) + minTweetLength;
        return constructRandomTweet(keywords, tweetLength);
    }

    private String constructRandomTweet(String[] keywords, int tweetLength) {
        StringBuilder tweet = new StringBuilder();
        for (int i = 0; i < tweetLength; i++) {
            tweet.append(WORDS[RANDOM.nextInt(WORDS.length)]).append(" ");
            if (i == tweetLength / 2) {
                tweet.append(keywords[RANDOM.nextInt(keywords.length)]).append(" ");
            }
        }
        tweet.append(".");
        return tweet.toString().trim();
    }

    private String formatTweetAsJsonWithParams(String[] params) {
        String tweet = tweetAsRawJson;
        for (int i = 0; i < params.length; i++) {
            tweet = tweet.replace("{" + i + "}", params[i]);
        }
        return tweet;
    }

    private void sleep(long sleepTimeMs) {
        try {
            Thread.sleep(sleepTimeMs);
        } catch (InterruptedException e) {
            throw new TwitterToKafkaServiceException("Error while sleeping for waiting new status to create!!");
        }
    }
}
