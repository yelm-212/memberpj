package com.yelm.memberpj.user;

import com.yelm.memberpj.config.dto.UserLoginDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    // TODO: Mapper Implementation
    User loginDtoToUser(UserLoginDto loginDto);
    UserDto.UserResponseDto userToUserResponseDto(User user);
}
