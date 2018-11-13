package example.service.controller;

import example.service.exception.ApiErrorResponse;
import example.service.exception.BillingPlanCreationException;
import example.service.exception.ErrorCode;
import example.service.model.BillingPlan;
import example.service.model.CountryCode;
import example.service.model.PriceDefinition;
import example.service.repository.BillingPlanRepository;
import example.service.repository.PriceDefinitionRepository;
import example.service.validator.BillingPlanValidator;
import example.service.validator.PriceDefinitionValidator;
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
import org.springframework.util.StringUtils;
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
    public Iterable<BillingPlan> billingPlans(@RequestParam(value = "country", required = false) final CountryCode country) {
        // TODO: Pagination
        if (StringUtils.isEmpty(country)) {
            return billingPlanRepository.findAll();
        }
        return billingPlanRepository.findByCountryCode(country);
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
            throw new BillingPlanCreationException(errorMsg, ErrorCode.DUPLICATE_BILLING_PLAN);
        }

        verifyAndSavePriceDefinitions(newBillingPlan, billingPlan.getPriceDefinitions());

        return newBillingPlan;
    }

    @GetMapping(value = "/{billingPlanId}/price-definitions")
    @ApiOperation(value = "Create a new price definition for this billing plan.", response = PriceDefinition[].class)
    public List<PriceDefinition> getPriceDefinitions(@PathVariable Integer billingPlanId) {
        return priceDefinitionRepository.findByBillingPlan_Id(billingPlanId);
    }

    @PostMapping(value = "/{billingPlanId}/price-definitions")
    @ApiOperation(value = "Create a new price definition for this billing plan.", response = PriceDefinition[].class)
    public List<PriceDefinition> addPriceDefinition(@PathVariable Integer billingPlanId, @Valid @RequestBody PriceDefinition priceDefinition) {
        Optional<BillingPlan> billingPlan = billingPlanRepository.findById(billingPlanId);
        if (!billingPlan.isPresent()) {
            throw new BillingPlanCreationException(String.format("No billing plan found with id=%d", billingPlanId), ErrorCode.VALIDATION_ERROR);
        }
        verifyAndSavePriceDefinitions(billingPlan.get(), Collections.singleton(priceDefinition));
        return priceDefinitionRepository.findByBillingPlan_Id(billingPlanId);
    }

    @GetMapping(value = "/{billingPlanId}/price-definitions/{priceId}")
    @ApiOperation(value = "Get a specific price definition for this billing plan.", response = PriceDefinition.class)
    public PriceDefinition patchPriceDefinition(@PathVariable Integer billingPlanId, @PathVariable Integer priceId) {
        Optional<BillingPlan> billingPlan = billingPlanRepository.findById(billingPlanId);
        if (!billingPlan.isPresent()) {
            throw new BillingPlanCreationException(String.format("No billing plan found with billingPlanId=%d", billingPlanId), ErrorCode.VALIDATION_ERROR);
        }
        Optional<PriceDefinition> optionalExistingPrice = priceDefinitionRepository.findById(priceId);
        if (!optionalExistingPrice.isPresent()) {
            throw new BillingPlanCreationException(String.format("No price definition found with id=%d", priceId), ErrorCode.VALIDATION_ERROR);
        }
        return optionalExistingPrice.get();
    }

    @PutMapping(value = "/{billingPlanId}/price-definitions/{priceId}")
    @ApiOperation(value = "Update an existing price definition for this billing plan.", response = PriceDefinition.class)
    public PriceDefinition patchPriceDefinition(@PathVariable Integer billingPlanId, @PathVariable Integer priceId, @Valid @RequestBody PriceDefinition updatedPriceDefinition) {
        Optional<BillingPlan> optionalBillingPlan = billingPlanRepository.findById(billingPlanId);
        if (!optionalBillingPlan.isPresent()) {
            throw new BillingPlanCreationException(String.format("No billing plan found with billingPlanId=%d", billingPlanId), ErrorCode.VALIDATION_ERROR);
        }
        BillingPlan billingPlan = optionalBillingPlan.get();
        Optional<PriceDefinition> optionalExistingPrice = billingPlan.getPriceDefinitions().stream().filter(p -> priceId.equals(p.getPriceDefinitionId())).findFirst();
        if (!optionalExistingPrice.isPresent()) {
            throw new BillingPlanCreationException(String.format("No price definition found with priceId=%d", priceId), ErrorCode.VALIDATION_ERROR);
        }
        PriceDefinition existingPrice = optionalExistingPrice.get();
        existingPrice.copyProperties(updatedPriceDefinition);
        priceDefinitionRepository.save(existingPrice);
        return priceDefinitionRepository.findById(priceId).get();
    }

    private void verifyPricePeriodsDoNotOverlap(final Integer billingPlanId, final Collection<PriceDefinition> priceDefinitions) {
        List<PriceDefinition> existingPriceDefinitions = priceDefinitionRepository.findByBillingPlan_Id(billingPlanId);
        for (PriceDefinition existingPrice : existingPriceDefinitions) {
            if (priceDefinitions.stream().anyMatch(newPrice -> newPrice.isOverlapping(existingPrice))) {
                String errorMsg = String.format("A price definition with an active period has already been defined for billing plan id=%d", billingPlanId);
                throw new BillingPlanCreationException(errorMsg, ErrorCode.OVERLAPPING_PRICE_PERIOD);
            }
        }
    }

    private void verifyAndSavePriceDefinitions(final BillingPlan billingPlan, final Collection<PriceDefinition> priceDefinitions) {
        verifyPricePeriodsDoNotOverlap(billingPlan.getId(), priceDefinitions);
        priceDefinitions.forEach(priceDefinition -> priceDefinition.setBillingPlan(billingPlan));
        priceDefinitionRepository.saveAll(priceDefinitions);
    }

}