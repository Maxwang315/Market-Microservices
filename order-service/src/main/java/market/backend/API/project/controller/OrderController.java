package market.backend.API.project.controller;

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
    public List<Order> getAllOrders() { return service.getAllOrders(); }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable int id) { return service.getOrderById(id); }

    @PostMapping
    public String addOrder(@Valid @RequestBody Order order) {
        service.addOrder(order);
        return "Order added";
    }

    @PutMapping
    public String updateOrder(@RequestBody Order order) {
        service.updateOrder(order);
        return "Order updated";
    }

    @DeleteMapping("/{id}")
    public String deleteOrder(@PathVariable int id) {
        service.deleteOrder(id);
        return "Order deleted";
    }

    @Value("${order.welcome:default value}")
    private String welcomeMessage;

    @GetMapping("/welcome")
    public String welcome() {
        return welcomeMessage;
    }

    @GetMapping("/product/{productId}")
    public ProductDTO getProduct(@PathVariable int productId) {
        return service.getProductForOrder(productId);
    }
}
