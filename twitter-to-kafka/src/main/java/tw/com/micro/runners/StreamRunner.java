package tw.com.micro.runners;

import twitter4j.TwitterException;

public interface StreamRunner {
    void start() throws TwitterException;
}
