package com.example.service.plan.repository;

import com.example.service.plan.model.ServiceBillingPlan;
import com.example.service.plan.model.ServiceOffering;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "billing-plans")
public interface ServiceBillingPlanRepository extends CrudRepository<ServiceBillingPlan, Integer> {
}
