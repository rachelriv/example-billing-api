package example.billing.service.repository;

import example.billing.service.model.Subscription;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface SubscriptionRepository extends CrudRepository<Subscription, UUID> {
}
