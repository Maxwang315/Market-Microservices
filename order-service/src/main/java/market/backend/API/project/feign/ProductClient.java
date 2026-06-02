package market.backend.API.project.feign;

import market.backend.API.project.config.FeignConfig;
import market.backend.API.project.entity.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", configuration = FeignConfig.class)
public interface ProductClient {

    @GetMapping("/Product/{id}")
    ProductDTO getProductById(@PathVariable int id);
}
