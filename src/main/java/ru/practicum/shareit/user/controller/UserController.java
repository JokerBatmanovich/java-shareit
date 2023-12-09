package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.exception.InvalidIdException;
import ru.practicum.shareit.user.dto.UserToGetDto;
import ru.practicum.shareit.user.dto.UserToReturnDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Validated
@RequiredArgsConstructor
public class UserController {
    @Qualifier("userServiceImpl")
    final UserService userService;

    @GetMapping
    public List<UserToReturnDto> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public UserToReturnDto get(@PathVariable Long userId) {
        return userService.getById(userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }

    @PostMapping()
    public UserToReturnDto add(@RequestBody @Validated({Create.class}) UserToGetDto userToGetDto) {
        return userService.add(userToGetDto);
    }

    @PatchMapping("/{userId}")
    public UserToReturnDto update(@RequestBody UserToGetDto userToGetDto, @PathVariable Long userId) {
        if (userToGetDto.getId() != null && !userToGetDto.getId().equals(userId)) {
            throw new InvalidIdException("ID тела запроса не совпадает с ID из параметров.");
        }
        userToGetDto.setId(userId);
        return userService.update(userToGetDto, userId);
    }

}
