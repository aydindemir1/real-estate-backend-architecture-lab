package com.aydindemir.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMessagingConfig {

    public static final String USER_PROFILE_EXCHANGE = "java44.auth.exchange";
    public static final String USER_PROFILE_QUEUE = "java44.user-profile.create.queue";
    public static final String USER_PROFILE_ROUTING_KEY = "user-profile.create";

    @Bean
    DirectExchange userProfileExchange() {
        return new DirectExchange(USER_PROFILE_EXCHANGE, true, false);
    }

    @Bean
    Queue userProfileCreateQueue() {
        return new Queue(USER_PROFILE_QUEUE, true);
    }

    @Bean
    Binding userProfileCreateBinding(
            Queue userProfileCreateQueue,
            DirectExchange userProfileExchange) {
        return BindingBuilder.bind(userProfileCreateQueue)
                .to(userProfileExchange)
                .with(USER_PROFILE_ROUTING_KEY);
    }

    @Bean
    MessageConverter rabbitMessageConverter() {
        return new JacksonJsonMessageConverter("com.aydindemir.messaging.model");
    }
}
