package example.billing.service.exception;


public enum ErrorCode {
    DUPLICATE_BILLING_PLAN,
    PRICE_DEFINITION_NOT_FOUND,
    BILLING_PLAN_NOT_FOUND,
    OVERLAPPING_PRICE_PERIOD,
    UNKNOWN_ERROR,
    VALIDATION_ERROR;
}
