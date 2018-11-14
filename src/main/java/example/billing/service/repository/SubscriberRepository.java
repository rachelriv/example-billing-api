package example.billing.service.repository;

import example.billing.service.model.CountryCode;
import example.billing.service.model.Subscriber;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface SubscriberRepository extends CrudRepository<Subscriber, Integer> {
    List<Subscriber> findByCountryCode(final CountryCode country);

}
