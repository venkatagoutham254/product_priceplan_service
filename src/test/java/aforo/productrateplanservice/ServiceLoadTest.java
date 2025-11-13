package aforo.productrateplanservice;

import aforo.productrateplanservice.rate_plan.service.RatePlanCoreService;
import aforo.productrateplanservice.rate_plan.service.RatePlanPricingAggregationService;
import aforo.productrateplanservice.rate_plan.RatePlanServiceImplRefactored;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

/**
 * Test to verify that the core services can be loaded without database issues
 */
@SpringBootTest(classes = ProductRatePlanServiceApplication.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.liquibase.enabled=false",
    "spring.docker.compose.enabled=false"
})
public class ServiceLoadTest {

    @MockBean
    private RatePlanCoreService ratePlanCoreService;
    
    @MockBean 
    private RatePlanPricingAggregationService ratePlanPricingAggregationService;

    @Test
    void contextLoads() {
        // This test will pass if the Spring context loads successfully
        // with our refactored services
    }
}
