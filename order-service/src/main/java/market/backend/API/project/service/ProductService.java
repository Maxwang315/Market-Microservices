package market.backend.API.project.service;

import market.backend.API.project.entity.Product;
import market.backend.API.project.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductMapper mapper;

    public Product getProductById(int id) {
        return mapper.selectById(id);
    }

    public List<Product> getAllProducts() {
        return mapper.selectList(null);
    }

    public void addProduct(Product product) {
        mapper.insert(product);
    }

    public void updateProduct(Product product) {
        mapper.updateById(product);
    }

    public void deleteProduct(int id) {
        mapper.deleteById(id);
    }
}
