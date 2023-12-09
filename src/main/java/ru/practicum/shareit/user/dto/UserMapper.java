package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {
    public UserToReturnDto toReturnDto(User user) {
        return new UserToReturnDto(user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public List<UserToReturnDto> toReturnDtoList(List<User> usersList) {
        List<UserToReturnDto> listToReturn = new ArrayList<>();
        usersList.forEach(user -> listToReturn.add(toReturnDto(user)));
        return listToReturn;
    }


    public User toEntity(UserToGetDto userToGetDto) {
        return new User(
                userToGetDto.getId(),
                userToGetDto.getName(),
                userToGetDto.getEmail()
        );
    }

    public User toEntity(UserToReturnDto userToReturnDto) {
        return new User(
                userToReturnDto.getId(),
                userToReturnDto.getName(),
                userToReturnDto.getEmail()
        );
    }

}
