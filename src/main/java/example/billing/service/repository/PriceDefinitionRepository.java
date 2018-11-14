package example.billing.service.repository;

import example.billing.service.model.PriceDefinition;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface PriceDefinitionRepository extends CrudRepository<PriceDefinition, Integer> {
    List<PriceDefinition> findByBillingPlan_Id(Integer billingPlanId);
}
