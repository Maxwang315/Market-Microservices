package market.backend.API.project.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import market.backend.API.project.entity.Product;
import market.backend.API.project.mapper.ProductMapper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ProductService {

    @Autowired
    private ProductMapper mapper;

    @Cacheable(value = "product", key = "#id")
    public Product getProductById(int id) {
        return mapper.selectById(id);
    }

    public List<Product> getAllProducts() {
        return mapper.selectList(null);
    }

    private RedissonClient redissonClient;

    public void addProduct(Product product) {
        String lockKey = "lock:product:" + product.getProductName();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean isLocked = lock.tryLock(3, 10, TimeUnit.SECONDS);

            if (!isLocked) {
                throw new RuntimeException("Request is being proceeded, please don't repeat");
            }

            //check if product already exists
            LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Product::getProductName, product.getProductName());
            if (mapper.selectOne(wrapper) != null) {
                throw new RuntimeException("Product already exists!!");
            }

            mapper.insert(product);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Lock interrupted");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @CacheEvict(value = "product", key = "#product.productId")
    public void updateProduct(Product product) {
        mapper.updateById(product);
    }

    @CacheEvict(value = "product", key = "#id")
    public void deleteProduct(int id) {
        mapper.deleteById(id);
    }
}
