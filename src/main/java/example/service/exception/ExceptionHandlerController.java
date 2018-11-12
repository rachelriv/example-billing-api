package example.service.exception;

import java.util.Date;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(value = { BillingPlanCreationException.class, SubscriptionCreationException.class })
    public ResponseEntity<ApiErrorResponse> duplicateBillingPlanExceptionHandler(EntityCreationException ex) {
        return new ResponseEntity<>(new ApiErrorResponse(new Date(), ex.getMessage(), ex.getErrorCode()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { MethodArgumentNotValidException.class })
    public ResponseEntity<ApiErrorResponse> handleException(MethodArgumentNotValidException exception) {

        String errorMsg = exception.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .findFirst()
                .orElse(exception.getMessage());

        return new ResponseEntity<>(new ApiErrorResponse(new Date(), errorMsg, ErrorCode.VALIDATION_ERROR), HttpStatus.BAD_REQUEST);
    }

}
