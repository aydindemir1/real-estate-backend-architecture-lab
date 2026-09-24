package com.aydindemir.manager;

import com.aydindemir.dto.request.UserProfileSaveRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-profile-service")
public interface IUserProfileManager {

    @PostMapping("/user/save")
    ResponseEntity<Boolean> save(@RequestBody UserProfileSaveRequestDto dto);
}
