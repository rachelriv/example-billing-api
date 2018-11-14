package example.billing.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerAutoConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("example.billing.service.api"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Billing Plan API")
                .description("Netflix has three service offerings (1S, 2S, & 4S) for customers based on the number of concurrent streams and priced accordingly.  " +
                        "As Netflix is a global service, it has defined prices for each service plan for each supported country.  " +
                        "For each customer, the service offering (1S, 2S, or 4S), price, and the country are known.  " +
                        "This API hosts prices for these three service offerings by country and enables us to systematically change prices across all global customers.  ")
                .contact(new Contact("Rachel Rivera", "https://github.com/rachelriv/example-billing-api/", "rachelriv94@gmail.com"))
                .version("1.0.0")
                .build();
    }
}