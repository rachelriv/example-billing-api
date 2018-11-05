package com.example.service.plan.repository;

import com.example.service.plan.model.ServiceOffering;
import io.swagger.annotations.Api;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Api(tags = "Service Offering Entity")
@RepositoryRestResource(path = "service-offerings")
public interface ServiceOfferingRepository extends CrudRepository<ServiceOffering, Long> {
}