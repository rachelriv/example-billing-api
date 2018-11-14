package example.billing.service.validator;

import example.billing.service.model.BillingPlan;
import example.billing.service.model.CountryCode;
import example.billing.service.model.PriceDefinition;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
public class BillingPlanValidator implements Validator {

    private static final Logger LOGGER = LoggerFactory.getLogger(BillingPlanValidator.class);

    private static final String LOCALE_LANGUAGE = "EN";

    @Override
    public boolean supports(Class<?> aClass) {
        return BillingPlan.class.equals(aClass);
    }

    @Override
    public void validate(@Nullable Object o, Errors errors) {
        BillingPlan billingPlan = (BillingPlan) o;
        if (billingPlan.getCountryCode() == null) {
            errors.rejectValue("countryCode", "billingPlan.countryCode", "Country code is required for billing plan.");
        } else {
            validateCurrency(billingPlan.getCountryCode(), billingPlan.getPriceDefinitions(), errors);
        }
    }

    private void validateCurrency(CountryCode countryCode, List<PriceDefinition> priceDefinitionList, Errors errors) {
        Locale locale = new Locale(LOCALE_LANGUAGE, countryCode.name());
        Currency expectedCurrency = Currency.getInstance(locale);
        if (priceDefinitionList == null || priceDefinitionList.isEmpty()) {
            errors.rejectValue("priceDefinitions", "priceDefinitions", "Price definition must be included for billing plan.");
        } else {
            for (PriceDefinition priceDefinition : priceDefinitionList) {
                if (!priceDefinition.getCurrencyCode().equals(expectedCurrency.getCurrencyCode())) {
                    String errorMsg =
                            String.format("Provided currency=%s does not match expected currency=%s for country=%s.",
                                    priceDefinition.getCurrencyCode(), expectedCurrency.getCurrencyCode(), countryCode.name());
                    errors.rejectValue("priceDefinitions", "priceDefinitions.currencyCode", errorMsg);
                }
            }
        }
    }
}
