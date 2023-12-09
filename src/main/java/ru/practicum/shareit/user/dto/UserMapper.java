package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {
    public UserToReturnDto toUserToReturnDto(User user) {
        return new UserToReturnDto(user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public List<UserToReturnDto> toUserToReturnDtoList(List<User> usersList) {
        List<UserToReturnDto> listToReturn = new ArrayList<>();
        usersList.forEach(user -> listToReturn.add(toUserToReturnDto(user)));
        return listToReturn;
    }


    public User toUser(UserToGetDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }

}
