package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.InvalidIdException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Validated
@Slf4j
@RequiredArgsConstructor
public class UserController {
    @Qualifier("userServiceImpl")
    final UserService userService;
    final UserMapper userMapper;

    @GetMapping
    public List<UserDto> getAll() {
        return userMapper.toUserDtoList(userService.getAll());
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable Long userId) {
        return userMapper.toUserDto(userService.getById(userId));
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }

    @PostMapping()
    public UserDto add(@RequestBody @Validated({Create.class}) UserDto userDto) {
        return userMapper.toUserDto(userService.add(userMapper.toUser(userDto)));
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable Long userId) {
        if (userDto.getId() == null || userDto.getId().equals(userId)) {
            userDto.setId(userId);
        } else {
            throw new InvalidIdException("ID тела запроса не совпадает с ID из параметров.");
        }
        return userMapper.toUserDto(userService.update(userMapper.toUser(userDto)));
    }

}
