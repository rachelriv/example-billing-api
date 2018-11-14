package example.billing.service.api;

import example.billing.service.exception.ApiErrorResponse;
import example.billing.service.exception.BillingPlanException;
import example.billing.service.exception.ErrorCode;
import example.billing.service.model.BillingPlan;
import example.billing.service.model.CountryCode;
import example.billing.service.model.PriceDefinition;
import example.billing.service.model.ServiceOffering;
import example.billing.service.repository.BillingPlanRepository;
import example.billing.service.repository.PriceDefinitionRepository;
import example.billing.service.validator.BillingPlanValidator;
import example.billing.service.validator.PriceDefinitionValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/v1/billing-plans")
@Api(tags = "Billing Plans", description = "Use the `/billing-plans` resource to create, update, show details for, and list plans.")
public class BillingPlanController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BillingPlanController.class);

    @Autowired
    private BillingPlanValidator billingPlanValidator;
    @Autowired
    private PriceDefinitionValidator priceDefinitionValidator;
    @Autowired
    private BillingPlanRepository billingPlanRepository;
    @Autowired
    private PriceDefinitionRepository priceDefinitionRepository;

    @InitBinder("billingPlan")
    public void setupBillingPlanBinder(WebDataBinder binder) {
        binder.addValidators(billingPlanValidator);
    }

    @InitBinder("priceDefinition")
    public void setupPriceDefinitionBinder(WebDataBinder binder) {
        binder.addValidators(priceDefinitionValidator);
    }


    @GetMapping(value = "/")
    @ApiOperation(value = "View a list of available billing plans.", response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list", response = BillingPlan[].class),
            @ApiResponse(code = 400, message = "Bad Request", response = ApiErrorResponse.class),
    }
    )
    public Iterable<BillingPlan> billingPlans(
            @RequestParam(name = "country", required = false) final CountryCode country,
            @RequestParam(name = "serviceOffering", required = false) final ServiceOffering serviceOffering) {
        // TODO: Pagination
        if (country != null && serviceOffering != null) {
            return Collections.singletonList(billingPlanRepository.findByCountryCodeAndServiceOffering(country, serviceOffering));
        } else if (country == null && serviceOffering == null) {
            return billingPlanRepository.findAll();
        } else if (country == null) {
            return billingPlanRepository.findByServiceOffering(serviceOffering);
        } else {
            return billingPlanRepository.findByCountryCode(country);
        }
    }

    @PostMapping(value = "/")
    @ApiOperation(value = "Create a new billing plan.", response = BillingPlan.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully created a new billing plan.", response = BillingPlan.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ApiErrorResponse.class),
    })
    public BillingPlan createNewBillingPlan(@Valid @RequestBody BillingPlan billingPlan) {
        BillingPlan newBillingPlan;
        try {
            newBillingPlan = billingPlanRepository.save(billingPlan);
        } catch (DataIntegrityViolationException ex) {
            String errorMsg =
                    String.format("A billing plan already exists for country=%s, serviceOffering=%s",
                            billingPlan.getCountryCode(), billingPlan.getServiceOffering());
            throw new BillingPlanException(errorMsg, ErrorCode.DUPLICATE_BILLING_PLAN);
        }

        verifyAndSavePriceDefinitions(newBillingPlan, billingPlan.getPriceDefinitions());

        return newBillingPlan;
    }

    @GetMapping(value = "/{billingPlanId}")
    @ApiOperation(value = "View a specific billing plan.", response = BillingPlan.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved billing plan.", response = BillingPlan.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ApiErrorResponse.class),
    }
    )
    public BillingPlan billingPlans(@PathVariable Integer billingPlanId) {
        return findBillingPlan(billingPlanId);
    }

    @GetMapping(value = "/{billingPlanId}/price-definitions")
    @ApiOperation(value = "Get price definitions for this billing plan.",
            response = PriceDefinition[].class,
            notes = "By default, only price definitions that are currently active are included. To include price definitions with expired or pending active periods, set `includeInactive` to `true`.")
    public List<PriceDefinition> getPriceDefinitions(@PathVariable Integer billingPlanId,
                                                     @RequestParam(name = "includeInactive", required = false, defaultValue = "false") final boolean includeInactivePriceDefinitions) {
        if (includeInactivePriceDefinitions) {
            return priceDefinitionRepository.findByBillingPlan_Id(billingPlanId);
        } else {
            return findBillingPlan(billingPlanId).getPriceDefinitions();
        }
    }

    @PostMapping(value = "/{billingPlanId}/price-definitions")
    @ApiOperation(value = "Create a new price definition for this billing plan.", response = PriceDefinition[].class)
    public List<PriceDefinition> addPriceDefinition(@PathVariable Integer billingPlanId, @Valid @RequestBody PriceDefinition priceDefinition) {
        BillingPlan billingPlan = findBillingPlan(billingPlanId);
        verifyAndSavePriceDefinitions(billingPlan, Collections.singleton(priceDefinition));
        return priceDefinitionRepository.findByBillingPlan_Id(billingPlanId);
    }

    @GetMapping(value = "/{billingPlanId}/price-definitions/{priceId}")
    @ApiOperation(value = "Get a specific price definition for this billing plan.", response = PriceDefinition.class)
    public PriceDefinition patchPriceDefinition(@PathVariable Integer billingPlanId, @PathVariable Integer priceId) {
        BillingPlan billingPlan = findBillingPlan(billingPlanId);
        return findPriceDefinition(billingPlan, priceId);
    }

    @PutMapping(value = "/{billingPlanId}/price-definitions/{priceId}")
    @ApiOperation(value = "Update an existing price definition for this billing plan.", response = PriceDefinition.class)
    public PriceDefinition patchPriceDefinition(@PathVariable Integer billingPlanId, @PathVariable Integer priceId, @Valid @RequestBody PriceDefinition updatedPriceDefinition) {
        BillingPlan billingPlan = findBillingPlan(billingPlanId);
        PriceDefinition existingPrice = findPriceDefinition(billingPlan, priceId);
        existingPrice.copyProperties(updatedPriceDefinition);
        priceDefinitionRepository.save(existingPrice);
        return priceDefinitionRepository.findById(priceId).get();
    }

    private BillingPlan findBillingPlan(final Integer id) {
        Optional<BillingPlan> billingPlan = billingPlanRepository.findById(id);
        if (billingPlan.isPresent()) {
            return billingPlan.get();
        } else {
            throw new BillingPlanException(String.format("No billing plan found with id=%d not found.", id), ErrorCode.BILLING_PLAN_NOT_FOUND);
        }
    }

    private PriceDefinition findPriceDefinition(final BillingPlan billingPlan, final Integer priceId) {
        Optional<PriceDefinition> optionalExistingPrice = billingPlan.getPriceDefinitions()
                .stream()
                .filter(p -> priceId.equals(p.getPriceDefinitionId()))
                .findFirst();
        if (!optionalExistingPrice.isPresent()) {
            throw new BillingPlanException(String.format("No price definition found with priceId=%d for billingPlanId=%d", priceId, billingPlan.getId()), ErrorCode.PRICE_DEFINITION_NOT_FOUND);
        }
        return optionalExistingPrice.get();
    }

    private void verifyPricePeriodsDoNotOverlap(final Integer billingPlanId, final Collection<PriceDefinition> priceDefinitions) {
        List<PriceDefinition> existingPriceDefinitions = priceDefinitionRepository.findByBillingPlan_Id(billingPlanId);
        for (PriceDefinition existingPrice : existingPriceDefinitions) {
            if (priceDefinitions.stream().anyMatch(newPrice -> newPrice.isOverlapping(existingPrice))) {
                String errorMsg = String.format("A price definition with an active period has already been defined for billing plan id=%d", billingPlanId);
                throw new BillingPlanException(errorMsg, ErrorCode.OVERLAPPING_PRICE_PERIOD);
            }
        }
    }

    private void verifyAndSavePriceDefinitions(final BillingPlan billingPlan, final Collection<PriceDefinition> priceDefinitions) {
        verifyPricePeriodsDoNotOverlap(billingPlan.getId(), priceDefinitions);
        priceDefinitions.forEach(priceDefinition -> priceDefinition.setBillingPlan(billingPlan));
        priceDefinitionRepository.saveAll(priceDefinitions);
    }

}