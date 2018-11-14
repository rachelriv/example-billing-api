package example.billing.service;

import example.billing.service.model.BillingPlan;
import example.billing.service.model.PriceDefinition;
import example.billing.service.model.PriceType;
import example.billing.service.model.ServiceOffering;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@AutoConfigureRestDocs
@SpringBootTest(classes={ExampleBillingPlanApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ExampleBillingPlanApplicationTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void contextLoads() {
	}

	@Test
	public void generateSwaggerDocs() throws Exception {
		String outputDir = System.getProperty("io.springfox.staticdocs.outputDir");
		String swaggerJson = this.restTemplate.getForObject("/v2/api-docs", String.class);
		Files.createDirectories(Paths.get(outputDir));
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputDir, "swagger.json"), StandardCharsets.UTF_8)){
			writer.write(swaggerJson);
		}
	}

	@Test
	public void testGetBillingPlanByCountry() throws Exception {
		ResponseEntity<BillingPlan[]> billingPlansUSResponse = this.restTemplate.getForEntity("/v1/billing-plans/?country=US", BillingPlan[].class);
		BillingPlan[] billingPlansUS = billingPlansUSResponse.getBody();
		assertEquals("There should be one billing plan defined for each of the three service offerings.", ServiceOffering.values().length, billingPlansUS.length);

		BillingPlan basicPlan = billingPlansUS[0];
		assertEquals(ServiceOffering.BASIC, basicPlan.getServiceOffering());
		List<PriceDefinition> basicPriceDefinitions = basicPlan.getPriceDefinitions();
		PriceDefinition regularBasicPriceDefinition = basicPriceDefinitions.get(0);
		assertEquals("USD", regularBasicPriceDefinition.getCurrencyCode());
		assertEquals("7.99", regularBasicPriceDefinition.getCurrencyValue());

		BillingPlan standardPlan = billingPlansUS[1];
		assertEquals(ServiceOffering.STANDARD, standardPlan.getServiceOffering());
		List<PriceDefinition> standardPlanPriceDefinitions = standardPlan.getPriceDefinitions();
		PriceDefinition regularStandardPriceDefinition = standardPlanPriceDefinitions.get(0);
		assertEquals("USD", regularStandardPriceDefinition.getCurrencyCode());
		assertEquals("10.99", regularStandardPriceDefinition.getCurrencyValue());

		BillingPlan premiumPlan = billingPlansUS[2];
		assertEquals(ServiceOffering.PREMIUM, premiumPlan.getServiceOffering());
		List<PriceDefinition> PremiumPlanPriceDefinitions = premiumPlan.getPriceDefinitions();
		PriceDefinition regularPremiumPriceDefinition = PremiumPlanPriceDefinitions.get(0);
		assertEquals("USD", regularPremiumPriceDefinition.getCurrencyCode());
		assertEquals("13.99", regularPremiumPriceDefinition.getCurrencyValue());
	}

	@Test
	public void testPostBillingPlan_addPriceDefinition() throws Exception {
		ResponseEntity<BillingPlan[]> billingPlansUSResponse = this.restTemplate.getForEntity("/v1/billing-plans/?country=US", BillingPlan[].class);
		BillingPlan billingPlanUS = billingPlansUSResponse.getBody()[0];
		Integer id = billingPlanUS.getId();

		// error: non-existent billing plan id
		PriceDefinition priceDefinition = new PriceDefinition(PriceType.REGULAR, "USD", "12.99", new Date(), new Date(System.currentTimeMillis()+10000));
		ResponseEntity  apiErrorResponseResponseEntity = this.restTemplate.postForEntity("/v1/billing-plans/123123123/price-definitions", priceDefinition, PriceDefinition.class);
		assertEquals(400, apiErrorResponseResponseEntity.getStatusCodeValue());

		// error: price definition expiration date is before activation date
		priceDefinition = new PriceDefinition(PriceType.REGULAR, "USD", "12.99", new Date(), new Date(System.currentTimeMillis()-10000));
		apiErrorResponseResponseEntity = this.restTemplate.postForEntity("/v1/billing-plans/"+id+"/price-definitions", priceDefinition, PriceDefinition.class);
		assertEquals(400, apiErrorResponseResponseEntity.getStatusCodeValue());

		// error: overlapping price definition
		PriceDefinition existingDefinition = billingPlanUS.getPriceDefinitions().get(0);
		priceDefinition = new PriceDefinition(PriceType.REGULAR, "USD", "12.99", existingDefinition.getActivationDate(), existingDefinition.getExpirationDate());
		apiErrorResponseResponseEntity = this.restTemplate.postForEntity("/v1/billing-plans/"+id+"/price-definitions", priceDefinition, PriceDefinition.class);
		assertEquals(400, apiErrorResponseResponseEntity.getStatusCodeValue());

		// success
		int previousSize = billingPlanUS.getPriceDefinitions().size();
		priceDefinition = new PriceDefinition(PriceType.REGULAR, "USD", "12.99", new Date(existingDefinition.getExpirationDate().getTime()+1), new Date(existingDefinition.getExpirationDate().getTime()+10000));
		ResponseEntity<PriceDefinition[]> billingPlansResponse = this.restTemplate.postForEntity("/v1/billing-plans/"+id+"/price-definitions", priceDefinition, PriceDefinition[].class);
		assertEquals(previousSize+1, billingPlansResponse.getBody().length);
		assertEquals(200, billingPlansResponse.getStatusCodeValue());
	}

}
