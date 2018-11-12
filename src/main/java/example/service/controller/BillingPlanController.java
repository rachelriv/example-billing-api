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
import org.springframework.web.bind.annotation.*;


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
    BillingPlanRepository billingPlanRepository;
    @Autowired
    PriceDefinitionRepository priceDefinitionRepository;

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

    @GetMapping(value = "/{id}/price-definitions")
    @ApiOperation(value = "Create a new price definition for this billing plan.", response = PriceDefinition[].class)
    public List<PriceDefinition> getPriceDefinitions(@PathVariable Integer id) {
        return priceDefinitionRepository.findByBillingPlan_Id(id);
    }

    @PostMapping(value = "/{id}/price-definitions")
    @ApiOperation(value = "Create a new price definition for this billing plan.", response = PriceDefinition[].class)
    public List<PriceDefinition> addPriceDefinition(@PathVariable Integer id, @Valid @RequestBody PriceDefinition priceDefinition) {
        Optional<BillingPlan> billingPlan = billingPlanRepository.findById(id);
        if (!billingPlan.isPresent()) {
            throw new BillingPlanCreationException(String.format("No billing plan found with id=%d", id), ErrorCode.VALIDATION_ERROR);
        }
        verifyAndSavePriceDefinitions(billingPlan.get(), Collections.singleton(priceDefinition));
        return priceDefinitionRepository.findByBillingPlan_Id(id);
    }

    @PutMapping(value = "/{id}/price-definitions/{priceId}")
    @ApiOperation(value = "Create a new price definition for this billing plan.", response = PriceDefinition.class)
    public PriceDefinition patchPriceDefinition(@PathVariable Integer id, @PathVariable Integer priceId, @Valid @RequestBody PriceDefinition priceDefinition) {
        Optional<BillingPlan> billingPlan = billingPlanRepository.findById(id);
        if (!billingPlan.isPresent()) {
            throw new BillingPlanCreationException(String.format("No billing plan found with id=%d", id), ErrorCode.VALIDATION_ERROR);
        }
        Optional<PriceDefinition> optionalExistingPrice = priceDefinitionRepository.findById(priceId);
        if (!optionalExistingPrice.isPresent()) {
            throw new BillingPlanCreationException(String.format("No price definition found with id=%d", priceId), ErrorCode.VALIDATION_ERROR);
        }
        PriceDefinition existingPrice = optionalExistingPrice.get();
        existingPrice.setActivationDate(priceDefinition.getActivationDate());
        existingPrice.setExpirationDate(priceDefinition.getExpirationDate());
        existingPrice.setCurrencyCode(priceDefinition.getCurrencyCode());
        existingPrice.setCurrencyValue(priceDefinition.getCurrencyValue());
        existingPrice.setPriceType(priceDefinition.getPriceType());
        return priceDefinitionRepository.save(existingPrice);
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