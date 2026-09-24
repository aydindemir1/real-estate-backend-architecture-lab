package com.aydindemir.mapper;

import com.aydindemir.dto.request.UserProfileSaveRequestDto;
import com.aydindemir.model.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IUserProfileMapper {

    UserProfile toUserProfile(UserProfileSaveRequestDto dto);
}
