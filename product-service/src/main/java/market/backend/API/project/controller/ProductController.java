package market.backend.API.project.controller;

import market.backend.API.project.common.Result;
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
    public Result<List<Product>> getAllProducts() { return Result.success(service.getAllProducts()); }

    @GetMapping("/{id}")
    public Result<Product> getProductById(@PathVariable int id) { return Result.success(service.getProductById(id)); }

    @PostMapping
    public Result<String> addProduct(@Valid @RequestBody Product product) {
        service.addProduct(product);
        return Result.success("Product added");
    }

    @PutMapping
    public Result<String> updateProduct(@RequestBody Product product) {
        service.updateProduct(product);
        return Result.success("Product updated");
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteProduct(@PathVariable int id) {
        service.deleteProduct(id);
        return Result.success("Product deleted");
    }

    @Value("${product.welcome:default value}")
    private String welcomeMessage;

    @GetMapping("/welcome")
    public Result<String> welcome() {
        return Result.success(welcomeMessage);
    }
}
