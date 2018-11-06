package com.example.service.plan.exception;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * Created by rrivera on 11/5/18.
 */
@ApiModel
@Data
class ApiErrorResponse {
    private String message;

}
