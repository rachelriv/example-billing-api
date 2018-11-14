package example.billing.service.api;

import example.billing.service.exception.ApiErrorResponse;
import example.billing.service.model.BillingPlan;
import example.billing.service.model.CountryCode;
import example.billing.service.model.ServiceOffering;
import example.billing.service.model.Subscriber;
import example.billing.service.model.Subscription;
import example.billing.service.repository.BillingPlanRepository;
import example.billing.service.repository.SubscriberRepository;
import example.billing.service.repository.SubscriptionRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/v1/subscribers")
@Api(tags = "Subscribers", description = "Use the `/subscribers` resource to create, update, show details for, and list subscribers.")
public class SubscriberController {

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private BillingPlanRepository billingPlanRepository;

    @GetMapping(value = "/")
    @ApiOperation(value = "View a list of users.", response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list", response = Subscriber[].class),
            @ApiResponse(code = 400, message = "Bad Request", response = ApiErrorResponse.class),
    }
    )
    public Iterable<Subscriber> users(@RequestParam(value = "country", required = false) final CountryCode country) {
        if (StringUtils.isEmpty(country)) {
            return subscriberRepository.findAll();
        }
        return subscriberRepository.findByCountryCode(country);
    }

    @PostMapping(value = "/")
    @ApiOperation(value = "Create a new user.", response = Subscriber.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully created a new subscriber.", response = Subscriber.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ApiErrorResponse.class),
    })
    public Subscriber createNewSubscriber(@RequestParam ServiceOffering serviceOffering, @RequestBody Subscriber subscriber) {
        Subscriber savedSubscriber = subscriberRepository.save(subscriber);
        BillingPlan billingPlan = billingPlanRepository.findByCountryCodeAndServiceOffering(subscriber.getCountryCode(), serviceOffering);
        Subscription subscription = new Subscription(savedSubscriber, billingPlan, new Date());
        subscriptionRepository.save(subscription);
        return subscriberRepository.findById(savedSubscriber.getId()).get();
    }

}
