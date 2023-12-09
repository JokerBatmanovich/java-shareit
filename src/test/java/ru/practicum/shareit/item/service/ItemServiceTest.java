package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.repository.JpaCommentRepository;
import ru.practicum.shareit.exception.NoPermissionException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemToGetDto;
import ru.practicum.shareit.item.dto.ItemToReturnDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.user.dto.UserToReturnDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
                classes = {ItemService.class, ItemMapper.class})
@ContextConfiguration(classes = {ItemServiceImpl.class, ItemMapper.class})
public class ItemServiceTest {
    @SpyBean
    ItemMapper itemMapper;
    @Autowired
    ItemService itemService;
    @MockBean JpaItemRepository itemRepository;
    @MockBean JpaUserRepository userRepository;
    @MockBean JpaBookingRepository bookingRepository;
    @MockBean JpaCommentRepository commentRepository;
    @MockBean BookingMapper bookingMapper;
    @MockBean CommentMapper commentMapper;

    User user1;
    UserToReturnDto user1ReturnDto;
    User user2;
    UserToReturnDto user2ReturnDto;

    Item item1;
    ItemToGetDto item1GetDto;
    ItemToReturnDto item1ReturnDto;

    Item item2;
    ItemToGetDto item2GetDto;
    ItemToReturnDto item2ReturnDto;

    Item item3;
    ItemToGetDto item3GetDto;
    ItemToReturnDto item3ReturnDto;

    @BeforeEach
    void setUp() {
        user1 = User.builder().id(1L).name("name1").email("email1@mail.ru").build();
        user1ReturnDto = UserToReturnDto.builder().id(1L).name("name1").email("email1@mail.ru").build();

        user2 = User.builder().id(2L).name("name2").email("email2@mail.ru").build();
        user2ReturnDto = UserToReturnDto.builder().id(2L).name("name2").email("email2@mail.ru").build();

        item1GetDto = ItemToGetDto.builder()
                .id(1L)
                .description("descr1")
                .name("name1")
                .available(true)
                .build();
        item1 = Item.builder()
                .id(1L)
                .name("name1")
                .owner(user1)
                .description("descr1")
                .build();
        item1ReturnDto = ItemToReturnDto.builder()
                .id(1L)
                .name("name1")
                .description("descr1")
                .comments(new ArrayList<>())
                .build();

        item2GetDto = ItemToGetDto.builder()
                .id(2L)
                .description("descr2")
                .name("name2")
                .build();
        item2 = Item.builder()
                .id(2L)
                .name("name2")
                .owner(user1)
                .description("descr2")
                .build();
        item2ReturnDto = ItemToReturnDto.builder()
                .id(2L)
                .name("name2")
                .description("descr2")
                .comments(new ArrayList<>())
                .build();

        item3GetDto = ItemToGetDto.builder()
                .id(3L)
                .description("descr3")
                .name("name3")
                .build();
        item3 = Item.builder()
                .id(3L)
                .name("name3")
                .owner(user2)
                .description("descr3")
                .build();
        item3ReturnDto = ItemToReturnDto.builder()
                .id(3L)
                .name("name3")
                .description("descr3")
                .comments(new ArrayList<>())
                .build();

    }

    @Test
    void getByOwnerId() {
        Mockito.when(userRepository.getReferenceById(user1.getId()))
                .thenReturn(user1);
        List<Item> itemsList = List.of(item1, item2);
        Mockito.when(itemRepository.findAllByOwnerIdOrderById(user1.getId(), PageRequest.of(0, 10)))
                .thenReturn(itemsList);
        List<ItemToReturnDto> actualList = itemService.getByOwnerId(user1.getId(), 0, 10);

        assertThat(actualList.size(), equalTo(2));
        assertTrue(actualList.containsAll(List.of(item1ReturnDto, item2ReturnDto)));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user1.getId());
        Mockito.verifyNoMoreInteractions(userRepository);

        Mockito.verify(itemRepository, Mockito.times(1))
                .findAllByOwnerIdOrderById(user1.getId(), PageRequest.of(0, 10));
        Mockito.verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void getByOwnerId_shouldThrowUserNotFoundException() {
        Mockito.when(userRepository.getReferenceById(user1.getId()))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(UserNotFoundException.class, () ->
                itemService.getByOwnerId(user1.getId(), 0, 10));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user1.getId());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verifyNoInteractions(itemMapper);
        Mockito.verifyNoInteractions(itemRepository);
    }

    @Test
    void add() {
        Mockito.when(userRepository.getReferenceById(user1.getId()))
                .thenReturn(user1);
        Mockito.when(itemRepository.save(item1))
                .thenReturn(item1);

        ItemToReturnDto actualItem = itemService.add(item1GetDto, user1.getId());

        assertThat(actualItem, equalTo(item1ReturnDto));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user1.getId());
        Mockito.verifyNoMoreInteractions(userRepository);

        Mockito.verify(itemRepository, Mockito.times(1))
                .save(item1);
        Mockito.verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void update() {
        Mockito.when(userRepository.getReferenceById(user1.getId()))
                .thenReturn(user1);
        Mockito.when(itemRepository.getReferenceById(item1.getId()))
                .thenReturn(item1);
        Mockito.when(commentRepository.findAllByItemId(item1.getId()))
                .thenReturn(new ArrayList<>());
        Mockito.when(bookingRepository.findAllByItemId(item1.getId()))
                .thenReturn(new ArrayList<>());

        Item updateItem = Item.builder()
                .id(item1.getId()).name("newName").description("newDescription").owner(user1).available(false).build();
        ItemToGetDto updateGetDto = ItemToGetDto.builder()
                .id(updateItem.getId())
                .name(updateItem.getName())
                .description(updateItem.getDescription())
                .available(updateItem.getAvailable())
                .build();
        ItemToReturnDto expectedItem = itemMapper.toReturnDto(updateItem, new ArrayList<>(), new ArrayList<>());

        Mockito.when(itemRepository.save(updateItem))
                .thenReturn(updateItem);

        ItemToReturnDto updatedItem = itemService.update(updateGetDto, item1GetDto.getId(), user1.getId());

        assertThat(updatedItem, equalTo(expectedItem));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user1.getId());
        Mockito.verifyNoMoreInteractions(userRepository);

        Mockito.verify(itemRepository, Mockito.times(1))
                .getReferenceById(item1.getId());
        Mockito.verify(itemRepository, Mockito.times(1))
                .save(updateItem);
        Mockito.verifyNoMoreInteractions(itemRepository);

        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemId(item1.getId());
        Mockito.verifyNoMoreInteractions(bookingRepository);

        Mockito.verify(commentRepository, Mockito.times(1))
                .findAllByItemId(item1.getId());
        Mockito.verifyNoMoreInteractions(commentRepository);

        Mockito.verify(bookingMapper, Mockito.times(1))
                .toForItemDtoList(new ArrayList<>());
        Mockito.verifyNoMoreInteractions(bookingMapper);
    }

    @Test
    void update_shouldThrowNoPermissionsException() {
        Mockito.when(userRepository.getReferenceById(user2.getId()))
                .thenReturn(user2);
        Mockito.when(itemRepository.getReferenceById(item1.getId()))
                .thenReturn(item1);

        assertThrows(NoPermissionException.class, () ->
                itemService.update(item1GetDto, item1.getId(), user2.getId()));

        Mockito.verifyNoInteractions(bookingMapper);
        Mockito.verifyNoInteractions(bookingRepository);
        Mockito.verifyNoInteractions(itemMapper);
        Mockito.verifyNoInteractions(commentRepository);
    }

    @Test
    void search() {
        List<Item> itemsList = List.of(item1, item2, item3);
        Mockito.when(itemRepository.search("name", PageRequest.of(0, 10)))
                .thenReturn(itemsList);
        Mockito.when(bookingRepository.findAllByItemId(anyLong()))
                .thenReturn(new ArrayList<>());
        Mockito.when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(new ArrayList<>());
        Mockito.when(bookingMapper.toForItemDtoList(any()))
                .thenReturn(new ArrayList<>());

        List<ItemToReturnDto> actualList = itemService.search("name", 0, 10);
        assertThat(actualList.size(), equalTo(3));
        assertTrue(actualList.containsAll(List.of(item1ReturnDto, item2ReturnDto, item3ReturnDto)));

        Mockito.verify(itemRepository, Mockito.times(1))
                .search("name", PageRequest.of(0, 10));
        Mockito.verifyNoMoreInteractions(itemRepository);

        Mockito.verify(bookingRepository, Mockito.times(3))
                .findAllByItemId(anyLong());
        Mockito.verifyNoMoreInteractions(itemRepository);

        Mockito.verify(commentRepository, Mockito.times(3))
                .findAllByItemId(anyLong());
        Mockito.verifyNoMoreInteractions(commentRepository);

        Mockito.verify(bookingMapper, Mockito.times(3))
                .toForItemDtoList(any());
        Mockito.verifyNoMoreInteractions(bookingMapper);
    }

    @Test
    void searchByBlankText_shouldReturnEmptyList() {
        List<ItemToReturnDto> actualList = itemService.search("", 0, 10);
        assertNotNull(actualList);
        assertThat(actualList.size(), equalTo(0));
        Mockito.verifyNoInteractions(itemRepository);
        Mockito.verifyNoInteractions(bookingRepository);
        Mockito.verifyNoInteractions(commentRepository);
        Mockito.verifyNoInteractions(bookingMapper);
    }

    @Test
    void searchByBigFrom_shouldReturnEmptyList() {
        List<Item> itemsList = List.of(item1, item2, item3);
        Mockito.when(itemRepository.search("name", PageRequest.of(2, 2)))
                .thenReturn(new ArrayList<>());

        List<ItemToReturnDto> actualList = itemService.search("name", 4,  2);

        assertNotNull(actualList);
        assertThat(actualList.size(), equalTo(0));

        Mockito.verify(itemRepository, Mockito.times(1))
                .search("name", PageRequest.of(2, 2));
        Mockito.verifyNoMoreInteractions(itemRepository);
        Mockito.verifyNoInteractions(bookingRepository);
        Mockito.verifyNoInteractions(commentRepository);
        Mockito.verifyNoInteractions(bookingMapper);
    }

    @Test
    void search_shouldReturnShortList() {
        List<Item> itemsList = List.of(item1, item2);
        Mockito.when(itemRepository.search("name", PageRequest.of(0, 2)))
                .thenReturn(itemsList);
        Mockito.when(bookingRepository.findAllByItemId(anyLong()))
                .thenReturn(new ArrayList<>());
        Mockito.when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(new ArrayList<>());
        Mockito.when(bookingMapper.toForItemDtoList(any()))
                .thenReturn(new ArrayList<>());

        List<ItemToReturnDto> actualList = itemService.search("name", 1,  2);

        assertThat(actualList.size(), equalTo(2));
        assertTrue(actualList.containsAll(List.of(item1ReturnDto, item2ReturnDto)));

        Mockito.verify(itemRepository, Mockito.times(1))
                .search("name", PageRequest.of(0, 2));

        Mockito.verify(bookingRepository, Mockito.times(2))
                .findAllByItemId(anyLong());
        Mockito.verifyNoMoreInteractions(itemRepository);

        Mockito.verify(commentRepository, Mockito.times(2))
                .findAllByItemId(anyLong());
        Mockito.verifyNoMoreInteractions(commentRepository);

        Mockito.verify(bookingMapper, Mockito.times(2))
                .toForItemDtoList(any());
        Mockito.verifyNoMoreInteractions(bookingMapper);
    }
}