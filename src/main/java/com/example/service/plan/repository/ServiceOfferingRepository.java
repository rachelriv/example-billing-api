package com.example.service.plan.repository;

import com.example.service.plan.model.ServiceOffering;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "service-offerings")
public interface ServiceOfferingRepository extends CrudRepository<ServiceOffering, Long> {
}