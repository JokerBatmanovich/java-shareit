package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestToGetDto;
import ru.practicum.shareit.request.dto.ItemRequestToReturnDto;
import ru.practicum.shareit.request.repository.JpaItemRequestRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserToGetDto;
import ru.practicum.shareit.user.dto.UserToReturnDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceIntegrationTest {
    @Autowired private ItemRequestService requestService;
    @Autowired private UserService userService;
    @Autowired private JpaUserRepository userRepository;
    @Autowired private UserMapper userMapper;
    @Autowired private JpaItemRequestRepository requestRepository;

    private User user2;

    private ItemRequestToReturnDto request1ReturnDto;
    private ItemRequestToReturnDto request2ReturnDto;
    private ItemRequestToReturnDto request4ReturnDto;

    @BeforeEach
    void setup() {
        UserToReturnDto user1ReturnDto = userService.add(
                UserToGetDto.builder().name("user1").email("user1@mail.ru").build());
        User user1 = userMapper.toEntity(user1ReturnDto);

        UserToReturnDto user2ReturnDto = userService.add(
                UserToGetDto.builder().name("user2").email("user2@mail.ru").build());
        user2 = userMapper.toEntity(user2ReturnDto);

        request1ReturnDto = requestService.add(
                ItemRequestToGetDto.builder().description("request1 descr").build(),  user1.getId());

        request2ReturnDto = requestService.add(
                ItemRequestToGetDto.builder().description("request2 descr").build(),  user1.getId());

        ItemRequestToReturnDto request3ReturnDto = requestService.add(
                ItemRequestToGetDto.builder().description("request3 descr").build(), user2.getId());

        request4ReturnDto = requestService.add(
                ItemRequestToGetDto.builder().description("request4 descr").build(), user1.getId());

        ItemRequestToReturnDto request5ReturnDto = requestService.add(
                ItemRequestToGetDto.builder().description("request5 descr").build(), user2.getId());
    }

    @AfterEach
    void clear() {
        userRepository.deleteAll();
        requestRepository.deleteAll();
    }

    @Test
    void getAll_shouldReturnAllUser2Requests() {
        List<ItemRequestToReturnDto> actualRequestsList = requestService.getAll(user2.getId(), 0, 10);
        assertThat(actualRequestsList.size(), equalTo(3));
        assertTrue(actualRequestsList.containsAll(List.of(request1ReturnDto, request2ReturnDto, request4ReturnDto)));
    }

    @Test
    void getAll_shouldReturnNotFullList() {
        List<ItemRequestToReturnDto> actualRequestsList = requestService.getAll(user2.getId(), 1, 2);
        assertThat(actualRequestsList.size(), equalTo(2));
        assertTrue(actualRequestsList.containsAll(List.of(request2ReturnDto, request4ReturnDto)));
    }

    @Test
    void getAll_shouldReturnEmptyList() {
        List<ItemRequestToReturnDto> actualRequestsList = requestService.getAll(user2.getId(), 5, 2);
        assertTrue(actualRequestsList.isEmpty());
    }

    @Test
    void getAll_shouldReturnOneElementList() {
        List<ItemRequestToReturnDto> actualRequestsList = requestService.getAll(user2.getId(), 0, 1);
        assertThat(actualRequestsList.size(), equalTo(1));
        assertTrue(actualRequestsList.contains(request4ReturnDto));
    }

    @Test
    void getAll_shouldThrowUserNotFoundException() {
        assertThrows(UserNotFoundException.class, () -> requestService.getAll(user2.getId() + 1, 0, 10));
    }

}
