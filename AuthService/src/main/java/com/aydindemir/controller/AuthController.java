package com.aydindemir.controller;

import com.aydindemir.dto.request.DoLoginRequestDto;
import com.aydindemir.dto.request.DoRegisterRequestDto;
import com.aydindemir.dto.response.DoRegisterResponseDto;
import com.aydindemir.model.Auth;
import com.aydindemir.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.aydindemir.constant.EndPoint.AUTH;
import static com.aydindemir.constant.EndPoint.FIND_ALL;
import static com.aydindemir.constant.EndPoint.GET_MESSAGE;
import static com.aydindemir.constant.EndPoint.LOGIN;
import static com.aydindemir.constant.EndPoint.REGISTER;
import static com.aydindemir.constant.EndPoint.REGISTER_ASYNC;

@RestController
@RequestMapping(AUTH)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(REGISTER)
    public ResponseEntity<DoRegisterResponseDto> register(@RequestBody DoRegisterRequestDto dto) {
        return ResponseEntity.ok(authService.doRegister(dto));
    }

    @PostMapping(REGISTER_ASYNC)
    public ResponseEntity<DoRegisterResponseDto> registerAsync(@RequestBody DoRegisterRequestDto dto) {
        return ResponseEntity.accepted().body(authService.doRegisterAsync(dto));
    }

    @PostMapping(LOGIN)
    public ResponseEntity<String> login(@RequestBody DoLoginRequestDto dto) {
        return ResponseEntity.ok(authService.doLogin(dto));
    }

    @GetMapping(FIND_ALL)
    public ResponseEntity<List<Auth>> findAll(@RequestParam("token") String token) {
        return ResponseEntity.ok(authService.findAll(token));
    }

    @GetMapping(GET_MESSAGE)
    public ResponseEntity<String> getMessage() {
        return ResponseEntity.ok("AuthService Day 2");
    }
}
