package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserToGetDto;
import ru.practicum.shareit.user.dto.UserToReturnDto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {
    @Autowired private UserService userService;


    @Test
    void updateTest() {
        UserToGetDto userDto = UserToGetDto.builder().name("name").email("email@mail.ru").build();
        UserToReturnDto oldUser = userService.add(userDto);

        UserToGetDto updatedUserDto = UserToGetDto.builder()
                .id(oldUser.getId()).name("Updated name").email("email@mail.ru").build();

        UserToReturnDto newUser = userService.update(updatedUserDto, oldUser.getId());

        UserToReturnDto expectedUser = UserToReturnDto.builder()
                .name(updatedUserDto.getName()).email(updatedUserDto.getEmail()).id(updatedUserDto.getId()).build();

        assertThat(newUser, equalTo(expectedUser));
    }

    @Test
    void updateTest_shouldThrowNotFoundException() {
        UserToGetDto userDto = UserToGetDto.builder().name("name").email("email@mail.ru").build();
        UserToReturnDto oldUser = userService.add(userDto);

        UserToGetDto updatedUserDto = UserToGetDto.builder()
                .id(oldUser.getId() + 1).name("Updated name").email("email@mail.ru").build();

        assertThrows(UserNotFoundException.class, () ->
                userService.update(updatedUserDto, updatedUserDto.getId()));
    }

}
