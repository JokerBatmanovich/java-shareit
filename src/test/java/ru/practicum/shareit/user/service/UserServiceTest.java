package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserToGetDto;
import ru.practicum.shareit.user.dto.UserToReturnDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
                classes = {UserService.class, UserMapper.class})
@ContextConfiguration(classes = {UserServiceImpl.class, UserMapper.class})
public class UserServiceTest {

    @Autowired private UserService userService;
    @SpyBean
    private UserMapper userMapper;

    @MockBean private JpaUserRepository userRepository;

    private UserToGetDto user1ToGetDto;
    private User user1Entity;
    private UserToReturnDto user1ToReturnDto;
    private User user2Entity;
    private UserToReturnDto user2ToReturnDto;
    private User user3Entity;
    private UserToReturnDto user3ToReturnDto;


    @BeforeEach
    void setUp() {
        user1ToGetDto = UserToGetDto.builder().id(1L).name("name1").email("email1@mail.ru").build();
        user1Entity = User.builder().id(1L).name("name1").email("email1@mail.ru").build();
        user1ToReturnDto = UserToReturnDto.builder().id(1L).name("name1").email("email1@mail.ru").build();

        user2Entity = User.builder().id(2L).name("name2").email("email2@mail.ru").build();
        user2ToReturnDto = UserToReturnDto.builder().id(2L).name("name2").email("email2@mail.ru").build();

        user3Entity = User.builder().id(3L).name("name3").email("email3@mail.ru").build();
        user3ToReturnDto = UserToReturnDto.builder().id(3L).name("name3").email("email3@mail.ru").build();
    }


    @Test
    void getById() {
        Mockito.when(userRepository.getReferenceById(1L))
                .thenReturn(user1Entity);
        UserToReturnDto actualUser = userService.getById(1L);
        assertThat(actualUser, equalTo(user1ToReturnDto));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(1L);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getById_shouldThrowException() {
        Mockito.when(userRepository.getReferenceById(10L))
                .thenThrow(EntityNotFoundException.class);
        assertThrows(UserNotFoundException.class, () -> {
            userService.getById(10L);
        });
        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(10L);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAll() {
        Mockito.when(userRepository.findAll())
                .thenReturn(List.of(user1Entity, user2Entity, user3Entity));
        List<UserToReturnDto> actualUsers = userService.getAll();
        assertThat(actualUsers.size(), equalTo(3));
        assertTrue(actualUsers.contains(user1ToReturnDto));
        assertTrue(actualUsers.contains(user2ToReturnDto));
        assertTrue(actualUsers.contains(user3ToReturnDto));

        Mockito.verify(userRepository, Mockito.times(1))
                .findAll();
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAll_shouldReturnEmptyList() {
        Mockito.when(userRepository.findAll())
                .thenReturn(new ArrayList<>());
        List<UserToReturnDto> actualUsers = userService.getAll();
        assertNotNull(actualUsers);
        assertThat(actualUsers.size(), equalTo(0));

        Mockito.verify(userRepository, Mockito.times(1))
                .findAll();
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void add() {
        Mockito.when(userMapper.toEntity(user1ToGetDto))
                        .thenReturn(user1Entity);
        Mockito.when(userRepository.save(user1Entity))
                .thenReturn(user1Entity);
        UserToReturnDto actualUser = userService.add(user1ToGetDto);
        assertThat(actualUser, equalTo(user1ToReturnDto));

        Mockito.verify(userRepository, Mockito.times(1))
                .save(user1Entity);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(userMapper, Mockito.times(1))
                .toEntity(user1ToGetDto);
        Mockito.verify(userMapper, Mockito.times(1))
                .toReturnDto(user1Entity);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void add_shouldThrowException() {
        Mockito.when(userMapper.toEntity(user1ToGetDto))
                .thenReturn(user1Entity);
        Mockito.when(userRepository.save(user1Entity))
                .thenThrow(DataIntegrityViolationException.class);
        assertThrows(EmailAlreadyExistException.class, () -> {
            userService.add(user1ToGetDto);
        });
        Mockito.verify(userRepository, Mockito.times(1))
                .save(user1Entity);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(userMapper, Mockito.times(1))
                .toEntity(user1ToGetDto);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

}
