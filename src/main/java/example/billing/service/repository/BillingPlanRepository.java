package example.billing.service.repository;

import example.billing.service.model.BillingPlan;
import example.billing.service.model.CountryCode;
import example.billing.service.model.ServiceOffering;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface BillingPlanRepository extends CrudRepository<BillingPlan, Integer> {
    List<BillingPlan> findByCountryCode(final CountryCode country);
    List<BillingPlan> findByServiceOffering(final ServiceOffering serviceOffering);
    BillingPlan findByCountryCodeAndServiceOffering(final CountryCode country, final ServiceOffering serviceOffering);
}
