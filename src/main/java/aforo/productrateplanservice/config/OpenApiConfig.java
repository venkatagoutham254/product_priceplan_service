package aforo.productrateplanservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Content;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // API Info
                .info(new Info()
                        .title("Aforo.ai Product Rate Plans Service API")
                        .description("API documentation for managing Product Rate Plans with multi-tenant support.")
                        .version("1.0.0")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                // JWT security scheme
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(In.HEADER)
                                .name("Authorization"))
                        // Error schemas
                        .addSchemas("ApiErrorResponse", new ObjectSchema()
                                .addProperty("status", new IntegerSchema())
                                .addProperty("code", new StringSchema())
                                .addProperty("message", new StringSchema())
                                .addProperty("fieldErrors", new ArraySchema().items(
                                        new Schema<>().$ref("ApiFieldError"))))
                        .addSchemas("ApiFieldError", new ObjectSchema()
                                .addProperty("code", new StringSchema())
                                .addProperty("message", new StringSchema())
                                .addProperty("property", new StringSchema())
                                .addProperty("rejectedValue", new ObjectSchema())
                                .addProperty("path", new StringSchema())))
                // Servers (migrated from SwaggerConfig)
                .servers(List.of(new Server().url("/").description("Default Server URL")));
    }

    @Bean
    public OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {
            operation.getResponses().addApiResponse("4xx/5xx", new ApiResponse()
                    .description("Error")
                    .content(new Content().addMediaType("*/*", new MediaType().schema(
                            new Schema<>().$ref("ApiErrorResponse")))));
            return operation;
        };
    }

    @Bean
    public GroupedOpenApi pricingGroup() {
        return GroupedOpenApi.builder()
                .group("Pricing")
                .pathsToMatch(
                        // Nested under rate plan
                        "/api/rateplans/**/flatfee/**",
                        "/api/rateplans/**/tiered/**",
                        "/api/rateplans/**/volume-pricing/**",
                        "/api/rateplans/**/usagebased/**",
                        "/api/rateplans/**/stairstep/**",
                        "/api/rateplans/**/discounts/**",
                        "/api/rateplans/**/minimumcommitments/**",
                        "/api/rateplans/**/setupfees/**",
                        "/api/rateplans/**/freemiums/**",
                        "/api/rateplans/**",
                        "/api/products/**",
                        "/api/product-rate-plans/**",
                        
                        // Standalone
                        "/api/estimator/**"
                )
                .build();
    }

    @Bean
    public GroupedOpenApi productsGroup() {
        return GroupedOpenApi.builder()
                .group("Products")
                .pathsToMatch(
                        "/api/products/**"
                )
                .build();
    }

    @Bean
    public GroupedOpenApi ratePlansGroup() {
        return GroupedOpenApi.builder()
                .group("Rate Plans")
                .pathsToMatch(
                        "/api/rateplans/**",
                        "/api/product-rate-plans/**"
                )
                .build();
    }
}
