package pl.michal.pacholczyk.restconsumer.restserver.configuration;

import com.google.common.base.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SpringFoxConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(getSwaggerPaths())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Rest Consumer Documentation")
                .description("Documentation contains description of available endpoints and related operations")
                .contact(new Contact("Michał Pacholczyk",
                        "https://www.linkedin.com/in/micha%C5%82-pacholczyk-8ba4b6124/",
                        "michalpacholczyk88@gmail.com"))
                .version("1.0.0")
                .build();
    }

    private Predicate<String> getSwaggerPaths() {
        return or(
                regex("/currency.*"),
                regex("/gold-price.*"),
                regex("/top-gold-price.*"));
    }
}
