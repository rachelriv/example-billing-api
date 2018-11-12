package example.service.data;

import com.github.javafaker.Faker;
import example.service.controller.SubscriberController;
import example.service.model.CountryCode;
import example.service.model.ServiceOffering;
import example.service.model.Subscriber;
import example.service.repository.BillingPlanRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
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
    private static final Random RANDOM = new Random();
    private static final String LOCALE_LANGUAGE = "EN";

    private static final int MOCK_SUBSCRIBER_COUNT = 100;

    @Autowired SubscriberController subscriberController;
    @Autowired BillingPlanRepository billingPlanRepository;

    private static CountryCode randomCountryCode(List<CountryCode> countryCodes)  {
        return countryCodes.get((RANDOM.nextInt(countryCodes.size())));
    }

    private static ServiceOffering randomServiceOffering()  {
        return ServiceOffering.values()[(RANDOM.nextInt(ServiceOffering.values().length))];
    }

    @PostConstruct
    public void createSubscribers() {
        Set<CountryCode> countryCodesWithBillingPlans = new HashSet<>();
        billingPlanRepository.findAll().forEach(plan -> countryCodesWithBillingPlans.add(plan.getCountryCode()));
        List<CountryCode> countryCodesWithBillingPlansList = new ArrayList<>(countryCodesWithBillingPlans);

        LOGGER.info("Constructing {} mock subscribers.", MOCK_SUBSCRIBER_COUNT);

        for (int i = 0; i < MOCK_SUBSCRIBER_COUNT; i++) {
            // TODO: bulk load
            CountryCode countryCode = randomCountryCode(countryCodesWithBillingPlansList);
            Locale locale = new Locale(LOCALE_LANGUAGE, countryCode.name());
            Subscriber subscriber = new Subscriber(countryCode);
            subscriber.setName(constructFaker(locale).name().fullName());
            subscriberController.createNewSubscriber(randomServiceOffering(), subscriber);
        }
    }

    private Faker constructFaker(final Locale locale) {
        try {
            return new Faker(locale);
        } catch (Exception e) {
            return new Faker(Locale.ENGLISH);
        }
    }
}