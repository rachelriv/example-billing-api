package example.billing.service.data;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.google.common.collect.Lists;
import example.billing.service.model.BillingPlan;
import example.billing.service.model.CountryCode;
import example.billing.service.model.Subscriber;
import example.billing.service.model.Subscription;
import example.billing.service.repository.BillingPlanRepository;
import example.billing.service.repository.SubscriberRepository;
import example.billing.service.repository.SubscriptionRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn(value = "mockBillingPlanLoader")
public class MockSubscriberCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockBillingPlanLoader.class);
    private static final String LOCALE_LANGUAGE = "EN";
    private static final int MOCK_SUBSCRIBER_COUNT_PER_COUNTRY = 10;
    private static final int MAX_COUNTRY_COUNT_TO_POPULATE_WITH_SUBSCRIBERS = 10;
    private static final Random RANDOM = new Random();

    // TODO: Don't construct as many mock subscribers/subscriptions for tests

    @Autowired
    private BillingPlanRepository billingPlanRepository;
    @Autowired
    private SubscriberRepository subscriberRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @PostConstruct
    public void createSubscribers() {
        Map<CountryCode, List<BillingPlan>> billingPlansByCountry = fetchSubsetOfBillingPlansByCountry();
        Map<CountryCode, List<Subscriber>> subscribersByCountry = new HashMap<>();
        billingPlansByCountry.forEach(((countryCode, billingPlans) -> {
            LOGGER.info("Constructing {} mock subscribers in country={}.", MOCK_SUBSCRIBER_COUNT_PER_COUNTRY, countryCode);
            Faker faker = constructFaker(new Locale(LOCALE_LANGUAGE, countryCode.name()));
            List<Subscriber> subscribers = new ArrayList<>();
            for (int i = 0; i < MOCK_SUBSCRIBER_COUNT_PER_COUNTRY; i++) {
                subscribers.add(createNewSubscriber(faker, countryCode));
            }
            subscribersByCountry.put(countryCode, subscribers);
        }));
        Iterable<Subscriber> savedSubscribers = subscriberRepository
                .saveAll(subscribersByCountry.values().stream().flatMap(List::stream).collect(Collectors.toList()));
        subscriptionRepository.saveAll(createSubscriptionsForSubscribers(Lists.newArrayList(savedSubscribers), billingPlansByCountry));
    }

    private List<Subscription> createSubscriptionsForSubscribers(final List<Subscriber> subscribers,
                                                                 final Map<CountryCode, List<BillingPlan>> billingPlansByCountry) {
        return subscribers
                .stream()
                .map(subscriber ->
                        new Subscription(
                                subscriber,
                                selectRandomBillingPlan(billingPlansByCountry.get(subscriber.getCountryCode())),
                                new Date()))
                .collect(Collectors.toList());
    }

    private BillingPlan selectRandomBillingPlan(final List<BillingPlan> billingPlans) {
        return billingPlans.get(RANDOM.nextInt(billingPlans.size()));
    }

    private Subscriber createNewSubscriber(final Faker faker, final CountryCode countryCode) {
        Name name = faker.name();
        String firstName = name.firstName();
        String lastName = name.lastName();

        String email = String.format("%s%s@example.com", firstName.toLowerCase(), lastName.toLowerCase());
        Subscriber subscriber = new Subscriber(email, countryCode);
        subscriber.setFirstName(firstName);
        subscriber.setLastName(lastName);

        return subscriber;
    }

    private Map<CountryCode, List<BillingPlan>> fetchSubsetOfBillingPlansByCountry() {
        return Lists.newArrayList(billingPlanRepository.findAll())
                .stream()
                .limit(MAX_COUNTRY_COUNT_TO_POPULATE_WITH_SUBSCRIBERS)
                .collect(Collectors.groupingBy(BillingPlan::getCountryCode));
    }

    private Faker constructFaker(final Locale locale) {
        try {
            return new Faker(locale);
        } catch (Exception e) {
            return new Faker(Locale.ENGLISH);
        }
    }
}