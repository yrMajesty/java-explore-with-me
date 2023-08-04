package ru.practicum.mainservice.service.mapper;

import org.mapstruct.Mapper;
import ru.practicum.mainservice.dto.user.UserDto;
import ru.practicum.mainservice.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    User fromDto(UserDto dto);

    List<UserDto> toDtos(List<User> users);
}
