package example.service.data;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import example.service.controller.BillingPlanController;
import example.service.model.BillingPlan;
import example.service.model.CountryCode;
import example.service.model.PriceDefinition;
import example.service.model.PriceType;
import example.service.model.ServiceOffering;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component(value = "mockBillingPlanLoader")
public class MockBillingPlanLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockBillingPlanLoader.class);

    @Value("${setup.data.billing-plans}")
    String billingPlanCSV;

    @Autowired
    BillingPlanController billingPlanController;

    private CsvSchema constructCsvSchemaForBillingPlans() {
        return CsvSchema.builder()
                .addColumn("currency")
                .addColumn("basicPrice")
                .addColumn("standardPrice")
                .addColumn("premiumPrice")
                .addColumn("countries")
                .build()
                .withSkipFirstDataRow(true);
    }

    private List<SerializedBillingPlan> loadSerializedBillingPlans() throws IOException{
        CsvMapper mapper = new CsvMapper();
        CsvSchema bootstrap = constructCsvSchemaForBillingPlans();

        mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream(billingPlanCSV);
        MappingIterator<SerializedBillingPlan> readValues =
                mapper.readerFor(SerializedBillingPlan.class).with(bootstrap).readValues(input);
        return readValues.readAll();
    }

    @PostConstruct
    public void loadBillingPlans() {
        try {
            List<SerializedBillingPlan> serializedBillingPlans = loadSerializedBillingPlans();
            LOGGER.info("Loading example billing plans from {}.", billingPlanCSV);
            serializedBillingPlans.forEach(
                    serializedBillingPlan -> Arrays.stream(serializedBillingPlan.getCountries().split(","))
                            .forEach(country -> saveBillingPlansForCountry(country, serializedBillingPlan))
            );

        } catch (Exception e) {
            LOGGER.error("Error occurred while loading billing plans.", e);
        }
    }

    private BillingPlan constructNewBillingPlan(final String country, final SerializedBillingPlan serializedBillingPlan, final ServiceOffering serviceOffering) {
        String priceValue = getPriceValueForServiceOffering(serializedBillingPlan, serviceOffering);
        PriceDefinition regularPriceDefinition = new PriceDefinition(PriceType.REGULAR, serializedBillingPlan.getCurrency(), priceValue, new Date(), createRandomFutureDate());
        PriceDefinition trialPriceDefinition = new PriceDefinition(PriceType.TRIAL, serializedBillingPlan.getCurrency(), "0.00", new Date(), createRandomFutureDate());
        return new BillingPlan(serviceOffering, CountryCode.valueOf(country), Arrays.asList(regularPriceDefinition, trialPriceDefinition));
    }

    private void saveBillingPlansForCountry(final String country, final SerializedBillingPlan serializedBillingPlan) {
        billingPlanController.createNewBillingPlan(constructNewBillingPlan(country, serializedBillingPlan, ServiceOffering.BASIC));
        billingPlanController.createNewBillingPlan(constructNewBillingPlan(country, serializedBillingPlan, ServiceOffering.STANDARD));
        billingPlanController.createNewBillingPlan(constructNewBillingPlan(country, serializedBillingPlan, ServiceOffering.PREMIUM));
    }

    private String getPriceValueForServiceOffering(SerializedBillingPlan serializedBillingPlan, ServiceOffering serviceOffering) {
        if (serviceOffering.equals(ServiceOffering.BASIC)) {
            return serializedBillingPlan.getBasicPrice();
        } else if (serviceOffering.equals(ServiceOffering.STANDARD)) {
            return serializedBillingPlan.getStandardPrice();
        } else {
            return serializedBillingPlan.getPremiumPrice();
        }
    }

    private Date createRandomFutureDate() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, new Random().nextInt(365));
        return c.getTime();
    }

}
