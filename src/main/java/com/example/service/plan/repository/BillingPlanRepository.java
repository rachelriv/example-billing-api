package com.example.service.plan.repository;

import com.example.service.plan.model.BillingPlan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "billing-plans")
public interface BillingPlanRepository extends CrudRepository<BillingPlan, Integer> {
}
