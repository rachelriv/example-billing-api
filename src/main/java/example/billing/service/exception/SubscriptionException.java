package example.billing.service.exception;

public class SubscriptionException extends EntityCreationException {
    public SubscriptionException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
