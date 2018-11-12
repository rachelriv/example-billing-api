package example.service.controller;

import example.service.exception.ApiErrorResponse;
import example.service.model.Subscription;
import example.service.repository.BillingPlanRepository;
import example.service.repository.SubscriberRepository;
import example.service.repository.SubscriptionRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/subscriptions")
@Api(tags = "Subscriptions", description = "Use the `/subscriptions` resource to create, update, show details for, and list user subscriptions.")
public class SubscriptionController {

    @Autowired SubscriptionRepository subscriptionRepository;
    @Autowired BillingPlanRepository billingPlanRepository;
    @Autowired
    SubscriberRepository subscriberRepository;

    @GetMapping(value = "/")
    @ApiOperation(value = "View a list of subscriptions.", response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list", response = Subscription[].class),
            @ApiResponse(code = 400, message = "Bad Request", response = ApiErrorResponse.class),
    }
    )
    public Iterable<Subscription> subscriptions() {
        return subscriptionRepository.findAll();
    }

}

