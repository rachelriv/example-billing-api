package com.example.service.plan.validator;

import com.example.service.plan.model.ServiceOffering;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Created by rrivera on 11/5/18.
 */
@Component
public class ServiceOfferingValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return ServiceOffering.class.equals(aClass);
    }

    @Override
    public void validate(@Nullable Object o, Errors errors) {
        ServiceOffering serviceOffering = (ServiceOffering) o;
        if (!ServiceOffering.ALLOWABLE_CONCURRENT_STREAM_COUNTS.contains(serviceOffering.getConcurrentStreamCount())) {
            String errorMsg = String.format("Allowable concurrentStreamCount values are %s", ServiceOffering.ALLOWABLE_CONCURRENT_STREAM_COUNTS);
            errors.rejectValue("concurrentStreamCount", "serviceOffering.concurrentStreamCount", errorMsg);
        }
    }
}
