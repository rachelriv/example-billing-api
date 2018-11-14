package example.billing.service.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class EntityCreationException extends RuntimeException {
    final String message;
    final ErrorCode errorCode;

    public EntityCreationException(final String message, final ErrorCode errorCode) {
        this.message = message;
        this.errorCode = errorCode;
    }

}
