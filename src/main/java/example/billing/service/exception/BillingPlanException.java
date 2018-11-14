package example.billing.service.exception;

public class BillingPlanException extends EntityCreationException {

    public BillingPlanException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
