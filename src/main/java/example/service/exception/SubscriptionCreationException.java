package example.service.exception;

public class SubscriptionCreationException extends EntityCreationException {
    public SubscriptionCreationException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
