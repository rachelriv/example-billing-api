package example.service.repository;

import example.service.model.CountryCode;
import example.service.model.Subscription;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface SubscriptionRepository extends CrudRepository<Subscription, String> {
    List<Subscription> findBySubscriber_CountryCode(CountryCode countryCode);
}
