package com.aydindemir.mapper;

import com.aydindemir.dto.request.DoRegisterRequestDto;
import com.aydindemir.dto.request.UserProfileSaveRequestDto;
import com.aydindemir.model.Auth;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IAuthMapper {

    Auth toAuth(DoRegisterRequestDto dto);

    @Mapping(target = "authId", source = "id")
    UserProfileSaveRequestDto fromAuth(Auth auth);
}
