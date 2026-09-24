package com.aydindemir.service;

import com.aydindemir.dto.request.UserProfileSaveRequestDto;
import com.aydindemir.mapper.IUserProfileMapper;
import com.aydindemir.model.UserProfile;
import com.aydindemir.repository.IUserProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService extends ServiceManager<UserProfile, Long> {

    private final IUserProfileMapper userProfileMapper;

    public UserProfileService(
            IUserProfileRepository repository,
            IUserProfileMapper userProfileMapper) {
        super(repository);
        this.userProfileMapper = userProfileMapper;
    }

    public boolean save(UserProfileSaveRequestDto dto) {
        UserProfile userProfile = userProfileMapper.toUserProfile(dto);
        userProfile.setState(true);
        userProfile.setCreatedAt(System.currentTimeMillis());

        save(userProfile);
        return true;
    }
}
