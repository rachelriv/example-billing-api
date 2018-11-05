package com.example.service.plan;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@AutoConfigureRestDocs
@SpringBootTest(classes={BillingPlanApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ServiceOfferingPlanApplicationTests {

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

}
