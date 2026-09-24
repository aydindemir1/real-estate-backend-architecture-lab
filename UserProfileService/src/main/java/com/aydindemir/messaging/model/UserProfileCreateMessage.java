package com.aydindemir.messaging.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileCreateMessage {

    private Long authId;
    private String username;
    private String email;
}
