package example.service.repository;

import example.service.model.BillingPlan;
import example.service.model.CountryCode;
import example.service.model.ServiceOffering;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface BillingPlanRepository extends CrudRepository<BillingPlan, Integer> {
    List<BillingPlan> findByCountryCode(final CountryCode country);
    BillingPlan findByCountryCodeAndServiceOffering(final CountryCode country, final ServiceOffering serviceOffering);
}
