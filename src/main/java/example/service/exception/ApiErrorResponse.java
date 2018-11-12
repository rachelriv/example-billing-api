package example.service.exception;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;


@ApiModel
@Data
@AllArgsConstructor
public class ApiErrorResponse {

    @ApiModelProperty
    private Date timestamp;

    @ApiModelProperty
    private String message;

    @ApiModelProperty
    @Enumerated(EnumType.STRING)
    private ErrorCode errorCode;

}