package aforo.productrateplanservice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(exclude = {RedisAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class})
@EnableJpaRepositories(basePackages = "aforo.productrateplanservice")
@EntityScan(basePackages = "aforo.productrateplanservice")
public class ProductRatePlanServiceApplication {
	public static void main(final String[] args) {
		SpringApplication.run(ProductRatePlanServiceApplication.class, args);
	}
}
