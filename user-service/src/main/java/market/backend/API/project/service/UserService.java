package market.backend.API.project.service;

import market.backend.API.project.entity.User;
import market.backend.API.project.mapper.UserMapper;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private UserMapper mapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public List<User> getAllUsers() {
        return mapper.selectList(null);
    }

    public User getUserById(int id) {
        String cacheKey = "user: " + id;
        Object cached = redisTemplate.opsForValue().get(cacheKey);

        if (cached != null) {
            if (cached.equals("NULL")) return null;
            return (User) cached;
        }

        String lockKey = "lock:query:user:" + id;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            lock.lock();

            cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                if (cached.equals("NULL")) return null;
                return (User) cached;
            }

            User user = mapper.selectById(id);

            if (user == null) {
                redisTemplate.opsForValue().set(cacheKey, "NULL", 5, TimeUnit.MINUTES);
                return null;
            }

            int randomMinutes = 30 + new Random().nextInt(10);
            redisTemplate.opsForValue().set(cacheKey, user, randomMinutes, TimeUnit.MINUTES);
            return user;
        } finally {
            lock.unlock();
        }
    }

    public void addUser(User user) {
        user.setCreatedAt(LocalDateTime.now());
        user.setIsActive(true);
        mapper.insert(user);

        //send welcome email message after registration
        String message = "Welcome to Market! " + user.getEmail();
        rocketMQTemplate.convertAndSend("user-register-topic", message);
    }

    public void updateUser(User user) {
        mapper.updateById(user);
        redisTemplate.delete("user:" + user.getId());
    }

    public void deleteUser(int id) {
        mapper.deleteById(id);
        redisTemplate.delete("user:" + id);
    }
}
