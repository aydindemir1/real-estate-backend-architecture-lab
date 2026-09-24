package com.aydindemir.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoRegisterRequestDto {

    private String username;
    private String email;
    private String password;
    private String rePassword;
}
