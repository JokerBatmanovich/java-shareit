package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemToGetDto;
import ru.practicum.shareit.item.dto.ItemToReturnDto;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserToGetDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTest {
    @Autowired ItemService itemService;
    @Autowired UserService userService;
    @Autowired JpaItemRepository itemRepository;
    @Autowired ItemMapper itemMapper;
    @Autowired UserMapper userMapper;
    @Autowired JpaUserRepository userRepository;
    @Autowired BookingService bookingService;
    @Autowired JpaBookingRepository bookingRepository;
    @Autowired BookingMapper bookingMapper;

    private User user1;

    private User user2;

    private ItemToReturnDto item1ReturnDto;
    private ItemToReturnDto item2ReturnDto;

    private Booking booking;


    @BeforeEach
    void setup() {
        UserToGetDto user1GetDto = UserToGetDto.builder().name("user1").email("email1@mail.ru").build();
        user1 = userMapper.toEntity(userService.add(user1GetDto));
        UserToGetDto user2GetDto = UserToGetDto.builder().name("user2").email("email2@mail.ru").build();
        user2 = userMapper.toEntity(userService.add(user2GetDto));

        ItemToGetDto item1GetDto = ItemToGetDto.builder().name("item1").description("descr1").available(true).build();
        item1ReturnDto = itemService.add(item1GetDto, user1.getId());
        ItemToGetDto item2GetDto = ItemToGetDto.builder().name("item2").description("descr2").available(false).build();
        item2ReturnDto = itemService.add(item2GetDto, user2.getId());

        booking = bookingRepository.save(Booking.builder()
                .booker(user1)
                .item(itemMapper.toEntity(item1ReturnDto, user1))
                .end(LocalDateTime.now().minusWeeks(1))
                .start(LocalDateTime.now().minusWeeks(2))
                .status(Status.WAITING)
                .build());
    }

    @AfterEach
    void clear() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void getByIdByOwner() {
        ItemToReturnDto actualItem = itemService.getById(item1ReturnDto.getId(), user1.getId());
        ItemToReturnDto expectedItem = item1ReturnDto;
        expectedItem.setLastBooking(bookingMapper.toForItemDto(booking));
        assertThat(actualItem, equalTo(expectedItem));

    }

    @Test
    void getByIdNotByOwner() {
        ItemToReturnDto actualItem = itemService.getById(item1ReturnDto.getId(), user2.getId());
        assertThat(actualItem, equalTo(item1ReturnDto));
    }

    @Test
    void getById_shouldThrowUserNotFoundException() {
        assertThrows(UserNotFoundException.class, () ->
                itemService.getById(item1ReturnDto.getId(),user2.getId() + 1));
    }

    @Test
    void getById_shouldThrowItemNotFoundException() {
        assertThrows(ItemNotFoundException.class, () ->
                itemService.getById(item2ReturnDto.getId() + 1,user2.getId()));
    }
}
