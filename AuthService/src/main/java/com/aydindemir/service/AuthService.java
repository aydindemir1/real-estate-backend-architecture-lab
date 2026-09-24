package com.aydindemir.service;

import com.aydindemir.dto.request.DoLoginRequestDto;
import com.aydindemir.dto.request.DoRegisterRequestDto;
import com.aydindemir.dto.response.DoRegisterResponseDto;
import com.aydindemir.exception.AuthServiceException;
import com.aydindemir.exception.ErrorType;
import com.aydindemir.manager.IUserProfileManager;
import com.aydindemir.mapper.IAuthMapper;
import com.aydindemir.messaging.model.UserProfileCreateMessage;
import com.aydindemir.messaging.producer.UserProfileCreateProducer;
import com.aydindemir.model.Auth;
import com.aydindemir.repository.IAuthRepository;
import com.aydindemir.utils.JwtTokenManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService extends ServiceManager<Auth, Long> {

    private final IAuthRepository authRepository;
    private final JwtTokenManager jwtTokenManager;
    private final IUserProfileManager userProfileManager;
    private final IAuthMapper authMapper;
    private final UserProfileCreateProducer userProfileCreateProducer;

    public AuthService(
            IAuthRepository authRepository,
            JwtTokenManager jwtTokenManager,
            IUserProfileManager userProfileManager,
            IAuthMapper authMapper,
            UserProfileCreateProducer userProfileCreateProducer) {
        super(authRepository);
        this.authRepository = authRepository;
        this.jwtTokenManager = jwtTokenManager;
        this.userProfileManager = userProfileManager;
        this.authMapper = authMapper;
        this.userProfileCreateProducer = userProfileCreateProducer;
    }

    public DoRegisterResponseDto doRegister(DoRegisterRequestDto dto) {
        Auth auth = createAuth(dto);

        userProfileManager.save(authMapper.fromAuth(auth));

        return toRegisterResponse(auth);
    }

    public DoRegisterResponseDto doRegisterAsync(DoRegisterRequestDto dto) {
        Auth auth = createAuth(dto);

        userProfileCreateProducer.publish(UserProfileCreateMessage.builder()
                .authId(auth.getId())
                .username(auth.getUsername())
                .email(auth.getEmail())
                .build());

        return toRegisterResponse(auth);
    }

    private Auth createAuth(DoRegisterRequestDto dto) {
        if (!dto.getPassword().equals(dto.getRePassword())) {
            throw new AuthServiceException(ErrorType.REGISTER_PASSWORD_MISMATCH);
        }

        if (authRepository.existsByUsername(dto.getUsername())) {
            throw new AuthServiceException(ErrorType.REGISTER_USERNAME_EXISTS);
        }

        Auth auth = authMapper.toAuth(dto);
        auth.setState(true);
        auth.setCreatedAt(System.currentTimeMillis());

        return save(auth);
    }

    private DoRegisterResponseDto toRegisterResponse(Auth auth) {
        return DoRegisterResponseDto.builder()
                .id(auth.getId())
                .username(auth.getUsername())
                .email(auth.getEmail())
                .build();
    }

    public String doLogin(DoLoginRequestDto dto) {
        Auth auth = authRepository.findByUsernameAndPassword(dto.getUsername(), dto.getPassword())
                .orElseThrow(() -> new AuthServiceException(ErrorType.LOGIN_USERNAME_OR_PASSWORD_MISMATCH));

        return jwtTokenManager.createToken(auth.getId())
                .orElseThrow(() -> new AuthServiceException(ErrorType.INTERNAL_SERVER_ERROR));
    }

    public List<Auth> findAll(String token) {
        Optional<Long> authId = jwtTokenManager.getIdInfoFromToken(token);

        if (authId.isEmpty() || findById(authId.get()).isEmpty()) {
            throw new AuthServiceException(ErrorType.INVALID_TOKEN);
        }

        return authRepository.findAll();
    }
}
