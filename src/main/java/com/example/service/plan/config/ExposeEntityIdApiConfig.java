package com.example.service.plan.config;

import com.example.service.plan.model.BillingPlan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

/**
 * Created by rrivera on 11/5/18.
 */
@Configuration
public class ExposeEntityIdApiConfig extends RepositoryRestConfigurerAdapter {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(BillingPlan.class);
    }

}