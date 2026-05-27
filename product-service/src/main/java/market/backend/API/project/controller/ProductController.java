package market.backend.API.project.controller;

import market.backend.API.project.entity.Product;
import market.backend.API.project.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RefreshScope
@RestController
@RequestMapping("/Product")
public class ProductController {

    @Autowired
    private ProductService service;

    @GetMapping
    private List<Product> getAllProducts() { return service.getAllProducts(); }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable int id) { return service.getProductById(id); }

    @PostMapping
    public String addProduct(@Valid @RequestBody Product product) {
        service.addProduct(product);
        return "Product added";
    }

    @PutMapping
    public String updateProduct(@RequestBody Product product) {
        service.updateProduct(product);
        return "Product updated";
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable int id) {
        service.deleteProduct(id);
        return "Product deleted";
    }

    @Value("${product.welcome:default value}")
    private String welcomeMessage;

    @GetMapping("/welcome")
    public String welcome() {
        return welcomeMessage;
    }
}
