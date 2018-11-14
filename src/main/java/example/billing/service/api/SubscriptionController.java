package example.billing.service.api;

import example.billing.service.exception.ApiErrorResponse;
import example.billing.service.model.Subscription;
import example.billing.service.repository.SubscriptionRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/subscriptions")
@Api(tags = "Subscriptions", description = "Use the `/subscriptions` resource to create, update, show details for, and list user subscriptions.")
public class SubscriptionController {

    @Autowired SubscriptionRepository subscriptionRepository;

    @GetMapping(value = "/{subscriptionId}")
    @ApiOperation(value = "View a list of subscriptions.", response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved subscription.", response = Subscription.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ApiErrorResponse.class),
    }
    )
    public Subscription findSubscription(@PathVariable UUID subscriptionId) {
        return subscriptionRepository.findById(subscriptionId).get();
    }

}

