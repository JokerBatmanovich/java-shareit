package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingToGetDto;
import ru.practicum.shareit.booking.dto.BookingToReturnDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.comment.repository.JpaCommentRepository;
import ru.practicum.shareit.exception.InvalidBookingTimeException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.NoPermissionException;
import ru.practicum.shareit.exception.UnavailableException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemToReturnDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserToReturnDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTest {
    @Autowired BookingService bookingService;
    @Autowired JpaBookingRepository bookingRepository;
    @Autowired JpaUserRepository userRepository;
    @Autowired JpaItemRepository itemRepository;
    @Autowired JpaCommentRepository commentRepository;
    @Autowired
    UserMapper userMapper;
    @Autowired
    ItemMapper itemMapper;

    private User user1;
    private User user2;

    private Item item1;
    private Item item2;
    private Item item3;

    private BookingToGetDto bookingGetDto;
    private BookingToReturnDto bookingReturnDto;


    @BeforeEach
    void setup() {
        user1 = userRepository.save(User.builder().name("user1").email("email1@mail.ru").build());
        user2 = userRepository.save(User.builder().name("user2").email("email2@mail.ru").build());
        UserToReturnDto user2ReturnDto = userMapper.toReturnDto(user2);

        item1 = itemRepository.save(Item.builder()
                .owner(user1).name("item1").description("descr1").available(true).build());
        ItemToReturnDto item1ReturnDto = ItemToReturnDto.builder()
                .id(item1.getId()).description("descr1").name("item1").available(true).comments(new ArrayList<>())
                .build();
        item2 = itemRepository.save(Item.builder()
                .owner(user2).name("item2").description("descr2").available(false).build());
        item3 = itemRepository.save(Item.builder()
                .owner(user2).name("item3").description("descr3").available(true).build());

        bookingGetDto = BookingToGetDto.builder()
                .start(LocalDateTime.now().plusWeeks(1))
                .end(LocalDateTime.now().plusWeeks(2))
                .itemId(item1.getId())
                .build();
        bookingReturnDto = BookingToReturnDto.builder()
                .booker(user2ReturnDto)
                .start(bookingGetDto.getStart())
                .end(bookingGetDto.getEnd())
                .item(item1ReturnDto)
                .status(Status.WAITING)
                .build();
    }

    @AfterEach
    void clear() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        commentRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    void add() {
        BookingToReturnDto actualBooking = bookingService.add(bookingGetDto, user2.getId(), item1.getId());
        assertNotNull(actualBooking.getId());
        assertThat(actualBooking.getItem(), equalTo(bookingReturnDto.getItem()));
        assertThat(actualBooking.getStart(), equalTo(bookingReturnDto.getStart()));
        assertThat(actualBooking.getEnd(), equalTo(bookingReturnDto.getEnd()));
        assertThat(actualBooking.getBooker(), equalTo(bookingReturnDto.getBooker()));
        assertThat(actualBooking.getStatus(), equalTo(bookingReturnDto.getStatus()));
    }

    @Test
    void add_shouldThrowInvalidBookingTimeException() {
        bookingGetDto.setEnd(LocalDateTime.now().minusWeeks(2));

        assertThrows(InvalidBookingTimeException.class, () ->
                bookingService.add(bookingGetDto, user2.getId(), item1.getId()));
    }

    @Test
    void add_shouldThrowItemNotFoundException() {
        assertThrows(ItemNotFoundException.class, () ->
                bookingService.add(bookingGetDto, user2.getId(), item3.getId() + 1));
    }

    @Test
    void add_shouldThrowNoPermissionException() {
        assertThrows(NoPermissionException.class, () ->
                bookingService.add(bookingGetDto, user2.getId(), item3.getId()));
    }

    @Test
    void add_shouldThrowUnavailableException() {
        assertThrows(UnavailableException.class, () ->
                bookingService.add(bookingGetDto, user1.getId(), item2.getId()));
    }
}
