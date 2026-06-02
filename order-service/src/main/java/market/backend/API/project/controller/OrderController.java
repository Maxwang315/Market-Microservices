package market.backend.API.project.controller;

import market.backend.API.project.common.Result;
import market.backend.API.project.entity.Order;
import market.backend.API.project.entity.ProductDTO;
import market.backend.API.project.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RefreshScope
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService service;

    @GetMapping
    public Result<List<Order>> getAllOrders() { return Result.success(service.getAllOrders()); }

    @GetMapping("/{id}")
    public Result<Order> getOrderById(@PathVariable int id) { return Result.success(service.getOrderById(id)); }

    @PostMapping
    public Result<String> addOrder(@Valid @RequestBody Order order) {
        service.addOrder(order);
        return Result.success("Order added");
    }

    @PutMapping
    public Result<String> updateOrder(@RequestBody Order order) {
        service.updateOrder(order);
        return Result.success("Order updated");
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteOrder(@PathVariable int id) {
        service.deleteOrder(id);
        return Result.success("Order deleted");
    }

    @Value("${order.welcome:default value}")
    private String welcomeMessage;

    @GetMapping("/welcome")
    public Result<String> welcome() { return Result.success(welcomeMessage); }

    @GetMapping("/product/{productId}")
    public Result<ProductDTO> getProduct(@PathVariable int productId) {
        return Result.success(service.getProductForOrder(productId));
    }
}
