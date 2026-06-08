package market.backend.API.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import market.backend.API.project.OrderCreatedMessage;
import market.backend.API.project.service.ProductService;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RocketMQMessageListener(
        topic = "order-topic",
        consumerGroup = "product-consumer-group"
)
public class OrderConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private ProductService productService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onMessage(MessageExt message) {
        String messageId = message.getMsgId();
        String redisKey = "processed:order:" + messageId;

        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            System.out.println("⚠️ Duplicate message, skipping: " + messageId);
            return;
        }

        try {
            String body = new String(message.getBody());
            OrderCreatedMessage orderMessage = objectMapper.readValue(body, OrderCreatedMessage.class);

            System.out.println("📦Order received, decreasing inventory");
            System.out.println("📦ProductId: " + orderMessage.getProductId() + ", Quantity: " + orderMessage.getQuantity());

            productService.decreaseInventory(orderMessage.getProductId(), orderMessage.getQuantity());

            redisTemplate.opsForValue().set(redisKey, "1", 24, TimeUnit.HOURS);

        } catch (Exception e) {
            System.out.println("❌ Failed to process order message: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
