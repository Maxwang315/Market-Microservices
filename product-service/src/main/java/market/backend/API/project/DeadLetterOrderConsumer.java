package market.backend.API.project;

import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener(
        topic = "%DLQ%product-consumer-group",
        consumerGroup = "dlq-order-consumer-group"
)
public class DeadLetterOrderConsumer implements RocketMQListener<MessageExt> {

    @Override
    public void onMessage(MessageExt message) {
        System.out.println("🚨 Dead letter order message: " + message.getMsgId());
        System.out.println("🚨 Body: " + new String(message.getBody()));
        // production: alert team, trigger refund, log to DB etc.
    }
}
