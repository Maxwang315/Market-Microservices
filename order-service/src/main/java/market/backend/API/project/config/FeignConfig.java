package market.backend.API.project.config;

import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public Retryer retryer() {
        //retry every 1 second, max 3 second wait, max 3 attempts
        return new Retryer.Default(1000, 3000, 3);
    }
}
