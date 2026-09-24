package com.aydindemir.messaging.consumer;

import com.aydindemir.dto.request.UserProfileSaveRequestDto;
import com.aydindemir.messaging.model.UserProfileCreateMessage;
import com.aydindemir.service.UserProfileService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.aydindemir.messaging.config.RabbitMessagingConfig.USER_PROFILE_QUEUE;

@Component
public class UserProfileCreateConsumer {

    private final UserProfileService userProfileService;

    public UserProfileCreateConsumer(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @RabbitListener(queues = USER_PROFILE_QUEUE)
    public void consume(UserProfileCreateMessage message) {
        userProfileService.save(UserProfileSaveRequestDto.builder()
                .authId(message.getAuthId())
                .username(message.getUsername())
                .email(message.getEmail())
                .build());
    }
}
