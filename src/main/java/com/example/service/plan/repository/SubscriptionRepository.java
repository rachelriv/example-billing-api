package com.example.service.plan.repository;

import com.example.service.plan.model.Subscription;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "billing-agreements")
public interface SubscriptionRepository extends CrudRepository<Subscription, String> {
}
