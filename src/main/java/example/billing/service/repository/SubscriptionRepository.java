package example.billing.service.repository;

import example.billing.service.model.CountryCode;
import example.billing.service.model.Subscription;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface SubscriptionRepository extends CrudRepository<Subscription, String> {
    List<Subscription> findBySubscriber_CountryCode(CountryCode countryCode);
}
