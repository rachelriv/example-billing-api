package com.example.service.plan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerAutoConfiguration {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Service Plan API")
                .description("Netflix offers customers 3 service plans (1S, 2S, & 4S) based on the number of concurrent streams and priced accordingly.  " +
                        "As we are a global service, we have defined prices for each service plan for the country we support.  " +
                        "For each customer, we know the service plan (1S, 2S, or 4S), the price for the chosen plan and the country of the customer." +
                        "This API hosts Netflix pricing which will enable us to systematically change prices across all our global customers (including changes pushed by country).")
                .contact(new Contact("Rachel Rivera", "http:/test-url.com", "rachel.nicole.rivera@gmail.com"))
                .version("1.0.0")
                .build();
    }

}
