package market.backend.API.project;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener(
        topic = "%DLQ%email-consumer-group",
        consumerGroup = "dlq-consumer-group"
)
public class DeadLetterConsumer implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        System.out.println("\uD83D\uDC80 Dead letter received: " + message);
        System.out.println("\uD83D\uDC80 Alert dev team / save to DB for manual review~~");
    }
}

