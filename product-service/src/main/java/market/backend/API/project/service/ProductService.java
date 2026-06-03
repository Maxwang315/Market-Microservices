package market.backend.API.project.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import market.backend.API.project.entity.Product;
import market.backend.API.project.mapper.ProductMapper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class ProductService {

    @Autowired
    private ProductMapper mapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    //manual caching
    public Product getProductById(int id) {
        //check cache first
        String cacheKey = "product:" + id;
        Object cached = redisTemplate.opsForValue().get(cacheKey);

        if (cached != null) {
            if (cached.equals("NULL")) return null; //cached null
            return (Product) cached;
        }

        // cache miss: use distributed lock to prevent breakdown
        String lockKey = "lock:query:product:" + id;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            lock.lock();

            //double check after getting lock
            //another thread might have already rebuilt cache
            cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                if (cached.equals("NULL")) return null;
                return (Product) cached;
            }

            //hit MySQL
            Product product = mapper.selectById(id);

            if (product == null) {
                //cache null for 5 minutes
                redisTemplate.opsForValue().set(cacheKey, "NULL", 5, TimeUnit.MINUTES);
                return null;
            }

            // cache real data for a random time from 30-40 minutes to prevent all of them
            //expiring at once
            int randomMinutes = 30 + new Random().nextInt(10);
            redisTemplate.opsForValue().set(cacheKey, product, randomMinutes, TimeUnit.MINUTES);
            return product;

        } finally {
            lock.unlock();
        }
    }

    public List<Product> getAllProducts() {
        return mapper.selectList(null);
    }

    @Autowired
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
