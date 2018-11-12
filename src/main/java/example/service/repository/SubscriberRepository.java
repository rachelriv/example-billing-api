package example.service.repository;

import example.service.model.CountryCode;
import example.service.model.Subscriber;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface SubscriberRepository extends CrudRepository<Subscriber, Integer> {
    List<Subscriber> findByCountryCode(final CountryCode country);

}
