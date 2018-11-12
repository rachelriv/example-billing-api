package example.service.repository;

import example.service.model.PriceDefinition;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface PriceDefinitionRepository extends CrudRepository<PriceDefinition, Long> {
    List<PriceDefinition> findByBillingPlan_Id(Integer billingPlanId);
}
