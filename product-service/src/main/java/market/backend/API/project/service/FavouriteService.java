package market.backend.API.project.service;

import market.backend.API.project.common.Result;
import market.backend.API.project.feign.UserClient;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class FavouriteService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private UserClient userClient;

    public String addFavourite(int userId, int productId) {
        //validate user exists
        Result<Object> userResult = userClient.getUserById(userId);
        if (userResult == null || userResult.getData() == null) {
            throw new RuntimeException("User not found!!");
        }

        String lockKey = "lock:favourite:" + userId + ":" + productId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            lock.lock();

            //check if already favourited using Set
            String userFavKey = "favourite:user:" + userId;
            boolean alreadyFavourited = redisTemplate.opsForSet()
                    .isMember(userFavKey, productId);

            if (alreadyFavourited) {
                return "Already Favourited!";
            }

            //add to user's favourite set
            redisTemplate.opsForSet().add(userFavKey, productId);

            //increment product favourite count using ZSet
            redisTemplate.opsForZSet().incrementScore("favourites:leaderboard",
                    "product:" + productId, 1);
            return "Favourited Successfully!!";
        } finally {
            lock.unlock();
        }
    }

    public String removeFavourite(int userId, int productId) {
        String userFavKey = "favourites:user:" + userId;
        redisTemplate.opsForSet().remove(userFavKey, productId);
        redisTemplate.opsForZSet().incrementScore("favourites:leaderboard",
                "product:" + productId, -1);
        return "Removed from favourites!!";
    }

    public Set<Object> getUserFavourites(int userId) {
        return redisTemplate.opsForSet().members("favourite:user:" + userId);
    }

    public long getProductFavouriteCount(int productId) {
        Double score = redisTemplate.opsForZSet()
                .score("favourites:leaderboard", "product:" + productId);
        return score == null ? 0L : score.longValue();
    }

    public Set<Object> getLeaderboard() {
        //top 10 most favourited products
        return redisTemplate.opsForZSet()
                .reverseRange("favourites:leaderboard", 0, 9);
    }
}
