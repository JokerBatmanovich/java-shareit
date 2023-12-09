package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestToGetDto;
import ru.practicum.shareit.request.dto.ItemRequestToReturnDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.JpaItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
                classes = {ItemRequestService.class, ItemRequestMapper.class})
@ContextConfiguration(classes = {ItemRequestServiceImpl.class, ItemRequestMapper.class})
public class ItemRequestServiceTest {
    @SpyBean
    ItemRequestMapper requestMapper;
    @Autowired
    ItemRequestService requestService;
    @MockBean
    private JpaItemRequestRepository requestRepository;
    @MockBean
    private JpaItemRepository itemRepository;
    @MockBean
    private JpaUserRepository userRepository;
    @MockBean
    private ItemMapper itemMapper;

    private User user1Entity;

    private ItemRequestToGetDto request1ToGetDto;
    private ItemRequest request1;
    private ItemRequestToReturnDto request1ToReturnDto;


    @BeforeEach
    void setUp() {
        user1Entity = User.builder().id(1L).name("name1").email("email1@mail.ru").build();

        request1ToGetDto = ItemRequestToGetDto.builder()
                .id(1L)
                .requesterId(user1Entity.getId())
                .description("description")
                .build();
        request1 = ItemRequest.builder()
                .id(1L)
                .requester(user1Entity)
                .description("description")
                .created(LocalDateTime.of(2023, 11, 29, 11, 0, 0))
                .build();
        request1ToReturnDto = ItemRequestToReturnDto.builder()
                .id(1L)
                .description("description")
                .created(LocalDateTime.of(2023, 11, 29, 11, 0, 0))
                .items(new ArrayList<>())
                .build();
    }


    @Test
    void getById() {
        Mockito.when(userRepository.getReferenceById(1L))
                .thenReturn(user1Entity);
        Mockito.when(requestRepository.getReferenceById(1L))
                .thenReturn(request1);
        Mockito.when(itemRepository.findAllByRequestId(1L))
                .thenReturn(new ArrayList<>());

        ItemRequestToReturnDto actualRequest = requestService.getById(1L, 1L);

        ItemRequestToReturnDto expectedRequest = request1ToReturnDto;
        assertThat(actualRequest, equalTo(expectedRequest));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(1L);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(requestRepository, Mockito.times(1))
                .getReferenceById(1L);
        Mockito.verifyNoMoreInteractions(requestRepository);
        Mockito.verify(requestMapper, Mockito.times(1))
                .toReturnDto(request1, new ArrayList<>());
        Mockito.verifyNoMoreInteractions(requestMapper);
    }

    @Test
    void getById_shouldReturnRequestNotFoundException() {
        Mockito.when(userRepository.getReferenceById(1L))
                .thenReturn(user1Entity);
        Mockito.when(requestRepository.getReferenceById(99L))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(ItemRequestNotFoundException.class, () -> {
            requestService.getById(99L, 1L);
        });

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(1L);
        Mockito.verifyNoMoreInteractions(userRepository);

        Mockito.verify(requestRepository, Mockito.times(1))
                .getReferenceById(99L);
        Mockito.verifyNoMoreInteractions(requestRepository);

        Mockito.verifyNoInteractions(itemRepository);

        Mockito.verifyNoInteractions(requestMapper);
    }

    @Test
    void getById_shouldReturnUserNotFoundException() {
        Mockito.when(userRepository.getReferenceById(99L))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> {
            requestService.getById(1L, 99L);
        });

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(99L);
        Mockito.verifyNoMoreInteractions(userRepository);

        Mockito.verifyNoInteractions(requestRepository);
        Mockito.verifyNoInteractions(itemRepository);
        Mockito.verifyNoInteractions(requestMapper);
    }

    @Test
    void add() {
        Mockito.when(userRepository.getReferenceById(1L))
                .thenReturn(user1Entity);

        Mockito.when(requestMapper.toEntity(request1ToGetDto, user1Entity))
                .thenReturn(request1);
        ItemRequest savedRequest1 = ItemRequest.builder()
                .requester(user1Entity)
                .id(1L)
                .description(request1.getDescription())
                .created(LocalDateTime.of(2023, 12, 10, 10, 0, 0))
                .build();
        Mockito.when(requestRepository.save(request1))
                .thenReturn(savedRequest1);

        ItemRequestToReturnDto actualRequest = requestService.add(request1ToGetDto, user1Entity.getId());

        assertThat(actualRequest.getId(), notNullValue());
        assertTrue(actualRequest.getItems().isEmpty());
        assertThat(actualRequest.getDescription(), equalTo(request1ToGetDto.getDescription()));
        assertThat(actualRequest.getCreated(), equalTo(LocalDateTime.of(2023, 12, 10, 10, 0, 0)));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(1L);
        Mockito.verifyNoMoreInteractions(userRepository);

        Mockito.verify(requestMapper, Mockito.times(1))
                .toEntity(request1ToGetDto, user1Entity);
        Mockito.verify(requestMapper, Mockito.times(1))
                .toReturnDto(savedRequest1, new ArrayList<>());
        Mockito.verifyNoMoreInteractions(requestMapper);
    }

    @Test
    void getByOwner() {
        Mockito.when(userRepository.getReferenceById(user1Entity.getId()))
                .thenReturn(user1Entity);

        ItemRequest request2 = ItemRequest.builder()
                .id(2L).description("descr2").requester(user1Entity).created(LocalDateTime.now()).build();
        ItemRequestToReturnDto request2ReturnDto = ItemRequestToReturnDto.builder()
                .id(2L)
                .items(new ArrayList<>())
                .description(request2.getDescription())
                .created(request2.getCreated())
                .build();
        List<ItemRequest> expectedList = List.of(
                request1, request2);
        Mockito.when(requestRepository.findAllByRequesterId(user1Entity.getId()))
                .thenReturn(expectedList);

        List<ItemRequestToReturnDto> actualList = requestService.getByOwner(user1Entity.getId());
        assertThat(actualList.size(), equalTo(2));
        assertTrue(actualList.containsAll(List.of(request1ToReturnDto, request2ReturnDto)));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user1Entity.getId());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(requestRepository, Mockito.times(1))
                .findAllByRequesterId(user1Entity.getId());
        Mockito.verifyNoMoreInteractions(requestRepository);
    }

    @Test
    void getByOwner_shouldThrowUserNotFoundException() {
        Mockito.when(userRepository.getReferenceById(user1Entity.getId() + 10))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> {
            requestService.getByOwner(user1Entity.getId() + 10);
        });

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user1Entity.getId() + 10);
        Mockito.verifyNoMoreInteractions(userRepository);

        Mockito.verifyNoInteractions(requestRepository);
    }

    @Test
    void getByOwner_shouldReturnEmptyList() {
        Mockito.when(userRepository.getReferenceById(1L))
                .thenReturn(user1Entity);
        Mockito.when(requestRepository.findAllByRequesterId(user1Entity.getId()))
                .thenReturn(new ArrayList<>());

        List<ItemRequestToReturnDto> actualList = requestService.getByOwner(1L);
        assertNotNull(actualList);
        assertThat(actualList.size(), equalTo(0));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user1Entity.getId());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(requestRepository, Mockito.times(1))
                .findAllByRequesterId(user1Entity.getId());
        Mockito.verifyNoMoreInteractions(requestRepository);
    }
}