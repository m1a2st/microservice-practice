package tw.com.micro.exceptions;

/**
 * Exception class for Kafka client error situations.
 */
public class KafkaClientException extends RuntimeException {

    public KafkaClientException(String message) {
        super(message);
    }

    public KafkaClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
