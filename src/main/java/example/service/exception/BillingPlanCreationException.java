package example.service.exception;

public class BillingPlanCreationException extends EntityCreationException {

    public BillingPlanCreationException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
