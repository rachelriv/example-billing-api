package example.billing.service.validator;

import example.billing.service.model.PriceDefinition;
import example.billing.service.repository.PriceDefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
public class PriceDefinitionValidator implements Validator {

    private static final Logger LOGGER = LoggerFactory.getLogger(BillingPlanValidator.class);

    @Autowired
    PriceDefinitionRepository priceDefinitionRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return PriceDefinition.class.equals(aClass);
    }

    @Override
    public void validate(@Nullable Object o, Errors errors) {
        PriceDefinition priceDefinition = (PriceDefinition) o;
        if (priceDefinition.getExpirationDate() != null && priceDefinition.getActivationDate() != null &&
                priceDefinition.getExpirationDate().before(priceDefinition.getActivationDate())) {
            String errorMsg = String.format("PriceDefinition=%s activation date should be before the expiration date.", priceDefinition);
            errors.rejectValue("activationDate", "priceDefinitions.activationDate", errorMsg);
            errors.rejectValue("expirationDate", "priceDefinitions.expirationDate", errorMsg);
        }
    }
}
