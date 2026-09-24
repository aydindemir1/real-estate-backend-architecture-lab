package com.aydindemir.messaging.producer;

import com.aydindemir.messaging.model.UserProfileCreateMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static com.aydindemir.messaging.config.RabbitMessagingConfig.USER_PROFILE_EXCHANGE;
import static com.aydindemir.messaging.config.RabbitMessagingConfig.USER_PROFILE_ROUTING_KEY;

@Component
public class UserProfileCreateProducer {

    private final RabbitTemplate rabbitTemplate;

    public UserProfileCreateProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(UserProfileCreateMessage message) {
        rabbitTemplate.convertAndSend(
                USER_PROFILE_EXCHANGE,
                USER_PROFILE_ROUTING_KEY,
                message);
    }
}
