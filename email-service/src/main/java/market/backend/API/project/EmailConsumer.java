package market.backend.API.project;

import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import market.backend.API.project.UserRegisterMessage;

import java.util.concurrent.TimeUnit;

@Service
@RocketMQMessageListener(
        topic = "user-register-topic",
        consumerGroup = "email-consumer-group"
)
public class EmailConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Override
    public void onMessage(MessageExt message) {
        String messageId = message.getMsgId();
        String redisKey = "processed:email:" + messageId;

        //check if already proceeded
        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            System.out.println("⚠️ Duplication message, skipping: " + messageId);
            return;
        }

        //process the message
        try {
            String body = new String(message.getBody());
            UserRegisterMessage registerMessage = objectMapper.readValue(body, UserRegisterMessage.class);
            System.out.println("📧 Sending welcome email to: " + registerMessage.getEmail());
            System.out.println("👤 Username: " + registerMessage.getUsername());
            System.out.println("👤 UserId: " + registerMessage.getUserId());
        } catch (Exception e) {
            System.out.println("❌ Failed to parse message: " + e.getMessage());
            throw new RuntimeException(e);
        }

        //mark as processed in Redis, keep for 24 hours
        redisTemplate.opsForValue().set(redisKey, "1", 24, TimeUnit.HOURS);
        System.out.println("✅ Marked as processed: " + messageId);
    }
}
