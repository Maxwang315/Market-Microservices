package market.backend.API.project.service;

import market.backend.API.project.entity.Order;
import market.backend.API.project.entity.ProductDTO;
import market.backend.API.project.feign.ProductClient;
import market.backend.API.project.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderMapper mapper;

    public Order getOrderById(int id) {
        return mapper.selectById(id);
    }

    public List<Order> getAllOrders() {
        return mapper.selectList(null);
    }

    public void addOrder(Order order) {
        mapper.insert(order);
    }

    public void updateOrder(Order order) {
        mapper.updateById(order);
    }

    public void deleteOrder(int id) {
        mapper.deleteById(id);
    }

    @Autowired
    private ProductClient productClient;

    public ProductDTO getProductForOrder(int productId) {
        return productClient.getProductById(productId);
    }
}
