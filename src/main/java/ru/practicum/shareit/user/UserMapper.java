package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {
    public UserDto toUserDto(User user) {
        return new UserDto(user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public List<UserDto> toUserDtoList(List<User> usersList) {
        List<UserDto> listToReturn = new ArrayList<>();
        usersList.forEach(user -> listToReturn.add(toUserDto(user)));
        return listToReturn;
    }


    public User toUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }

}
