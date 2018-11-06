package com.example.service.plan.config;

import com.example.service.plan.validator.ServiceOfferingValidator;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

/**
 * Created by rrivera on 11/5/18.
 */
@Configuration
public class RestValidationConfig extends RepositoryRestConfigurerAdapter {
    @Override
    public void configureValidatingRepositoryEventListener(ValidatingRepositoryEventListener v) {
        v.addValidator("beforeCreate", new ServiceOfferingValidator());
    }
}
