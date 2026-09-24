package com.aydindemir.controller;

import com.aydindemir.dto.request.UserProfileSaveRequestDto;
import com.aydindemir.model.UserProfile;
import com.aydindemir.service.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.aydindemir.constant.EndPoint.FIND_ALL;
import static com.aydindemir.constant.EndPoint.SAVE;
import static com.aydindemir.constant.EndPoint.USER;

@RestController
@RequestMapping(USER)
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/hello")
    public String hello() {
        return "UserProfileService Hi";
    }

    @PostMapping(SAVE)
    public ResponseEntity<Boolean> save(@RequestBody UserProfileSaveRequestDto dto) {
        userProfileService.save(dto);
        return ResponseEntity.ok(Boolean.TRUE);
    }

    @GetMapping(FIND_ALL)
    public ResponseEntity<List<UserProfile>> findAll() {
        return ResponseEntity.ok(userProfileService.findAll());
    }
}
