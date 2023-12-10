package ru.practicum.shareit.booking.service;

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
import ru.practicum.shareit.booking.dto.BookingToGetDto;
import ru.practicum.shareit.booking.dto.BookingToReturnDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.comment.repository.JpaCommentRepository;
import ru.practicum.shareit.exception.IllegalStateException;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemToReturnDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.user.dto.UserToReturnDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
                classes = {BookingService.class, BookingMapper.class})
@ContextConfiguration(classes = {BookingServiceImpl.class, BookingMapper.class})
public class BookingServiceTest {
    @SpyBean private BookingMapper bookingMapper;
    @Autowired private BookingService bookingService;
    @MockBean private ItemMapper itemMapper;
    @MockBean private JpaItemRepository itemRepository;
    @MockBean private JpaUserRepository userRepository;
    @MockBean private JpaBookingRepository bookingRepository;
    @MockBean private JpaCommentRepository commentRepository;

    User user1;
    UserToReturnDto user1ReturnDto;
    User user2;
    UserToReturnDto user2ReturnDto;
    User user3;
    UserToReturnDto user3ReturnDto;

    Item item1;
    ItemToReturnDto item1ReturnDto;
    Item item2;
    ItemToReturnDto item2ReturnDto;

    BookingToGetDto booking1GetDto;
    Booking booking1;
    BookingToReturnDto booking1ReturnDto;

    BookingToGetDto booking2GetDto;
    Booking booking2;
    BookingToReturnDto booking2ReturnDto;

    BookingToGetDto booking3GetDto;
    Booking booking3;
    BookingToReturnDto booking3ReturnDto;

    BookingToGetDto booking4GetDto;
    Booking booking4;
    BookingToReturnDto booking4ReturnDto;

    @BeforeEach
    void setUp() {
        user1 = User.builder().id(1L).name("name1").email("email1@mail.ru").build();
        user1ReturnDto = UserToReturnDto.builder().id(1L).name("name1").email("email1@mail.ru").build();

        user2 = User.builder().id(2L).name("name2").email("email2@mail.ru").build();
        user2ReturnDto = UserToReturnDto.builder().id(2L).name("name2").email("email2@mail.ru").build();

        user3 = User.builder().id(3L).name("name3").email("email3@mail.ru").build();
        user3ReturnDto = UserToReturnDto.builder().id(3L).name("name3").email("email3@mail.ru").build();

        item1 = Item.builder().id(1L).name("item1").description("descr1").available(true).owner(user1).build();
        item1ReturnDto = ItemToReturnDto.builder().id(1L).name("item1").description("descr1").available(true)
                .comments(new ArrayList<>()).build();
        item2 = Item.builder().id(2L).name("item2").description("descr2").available(true).owner(user2).build();
        item2ReturnDto = ItemToReturnDto.builder().id(2L).name("item2").description("descr2").available(true)
                .comments(new ArrayList<>()).build();

        booking1GetDto = BookingToGetDto.builder()
                .id(1L).start(LocalDateTime.now().minusWeeks(2)).end(LocalDateTime.now().minusWeeks(1)).itemId(1L)
                .build();
        booking1 = Booking.builder()
                .id(1L).start(booking1GetDto.getStart()).end(booking1GetDto.getEnd()).item(item1).booker(user2)
                .status(Status.WAITING)
                .build();
        booking1ReturnDto = BookingToReturnDto.builder()
                .id(1L).start(booking1.getStart()).end(booking1.getEnd()).item(item1ReturnDto).booker(user2ReturnDto)
                .status(Status.WAITING).build();

        booking2GetDto = BookingToGetDto.builder()
                .id(3L).start(LocalDateTime.now().minusWeeks(2)).end(LocalDateTime.now().minusWeeks(1)).itemId(2L)
                .build();
        booking2 = Booking.builder()
                .id(3L).start(booking2GetDto.getStart()).end(booking2GetDto.getEnd()).item(item2).booker(user1)
                .status(Status.WAITING)
                .build();
        booking2ReturnDto = BookingToReturnDto.builder()
                .id(3L).start(booking2.getStart()).end(booking2.getEnd()).item(item2ReturnDto).booker(user1ReturnDto)
                .status(Status.WAITING).build();

        booking3GetDto = BookingToGetDto.builder()
                .id(3L).start(LocalDateTime.now().minusWeeks(2)).end(LocalDateTime.now().minusWeeks(1)).itemId(1L)
                .build();
        booking3 = Booking.builder()
                .id(3L).start(booking3GetDto.getStart()).end(booking3GetDto.getEnd()).item(item1)
                .booker(user3)
                .status(Status.APPROVED)
                .build();
        booking3ReturnDto = BookingToReturnDto.builder()
                .id(3L).start(booking3.getStart()).end(booking3.getEnd()).item(item1ReturnDto).booker(user3ReturnDto)
                .status(Status.APPROVED).build();

        booking4GetDto = BookingToGetDto.builder()
                .id(1L).start(booking1.getStart()).end(booking1.getEnd().plusWeeks(1)).itemId(1L)
                .build();
        booking4 = Booking.builder()
                .id(1L).start(booking4GetDto.getStart()).end(booking4GetDto.getEnd()).item(item1)
                .booker(user2)
                .status(Status.WAITING)
                .build();
        booking4ReturnDto = BookingToReturnDto.builder()
                .id(1L).start(booking4.getStart()).end(booking4.getEnd()).item(item1ReturnDto).booker(user2ReturnDto)
                .status(Status.WAITING).build();
    }

    @Test
    void getById() {
        Mockito.when(userRepository.getReferenceById(user2.getId()))
                .thenReturn(user2);
        Mockito.when(bookingRepository.getReferenceById(booking1.getId()))
                .thenReturn(booking1);
        Mockito.when(bookingRepository.findAllByItemId(booking1.getItem().getId()))
                .thenReturn(new ArrayList<>());
        Mockito.when(commentRepository.findAllByItemId(booking1.getItem().getId()))
                .thenReturn(new ArrayList<>());
        Mockito.when(itemMapper.toReturnDto(item1, new ArrayList<>(), new ArrayList<>()))
                .thenReturn(item1ReturnDto);

        BookingToReturnDto actualBooking = bookingService.getById(booking1.getId(), user2.getId());

        assertThat(actualBooking, equalTo(booking1ReturnDto));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user2.getId());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .getReferenceById(booking1.getId());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemId(booking1.getItem().getId());
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(commentRepository, Mockito.times(1))
                .findAllByItemId(booking1.getItem().getId());
        Mockito.verifyNoMoreInteractions(commentRepository);
        Mockito.verify(itemMapper, Mockito.times(1))
                .toReturnDto(item1, new ArrayList<>(), new ArrayList<>());
        Mockito.verifyNoMoreInteractions(itemMapper);
    }

    @Test
    void getById_shouldThrowUserNotFoundException() {
        Mockito.when(userRepository.getReferenceById(user3.getId() + 1))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(UserNotFoundException.class, () ->
                bookingService.getById(booking1.getId(), user3.getId() + 1));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user3.getId() + 1);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verifyNoInteractions(bookingRepository);
        Mockito.verifyNoInteractions(commentRepository);
        Mockito.verifyNoInteractions(itemMapper);
    }

    @Test
    void getById_shouldThrowBookingNotFoundException() {
        Mockito.when(userRepository.getReferenceById(user2.getId()))
                .thenReturn(user2);
        Mockito.when(bookingRepository.getReferenceById(booking1.getId() + 10))
                .thenThrow(EntityNotFoundException.class);
        assertThrows(BookingNotFoundException.class, () ->
                bookingService.getById(booking1.getId() + 10, user2.getId()));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user2.getId());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .getReferenceById(booking1.getId() + 10);
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verifyNoInteractions(commentRepository);
        Mockito.verifyNoInteractions(itemMapper);
    }

    @Test
    void getById_shouldThrowNoPermissionsException() {
        Mockito.when(userRepository.getReferenceById(user3.getId()))
                .thenReturn(user3);
        Mockito.when(bookingRepository.getReferenceById(booking1.getId()))
                .thenReturn(booking1);
        assertThrows(NoPermissionException.class, () -> bookingService.getById(booking1.getId(), user3.getId()));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user3.getId());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .getReferenceById(booking1.getId());
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verifyNoInteractions(commentRepository);
        Mockito.verifyNoInteractions(itemMapper);
    }

    @Test
    void getBookingsByState_getByCurrent() {
        Mockito.when(userRepository.getReferenceById(user1.getId()))
                .thenReturn(user1);
        List<Booking> bookings = List.of(booking1,booking2);
        Mockito.when(bookingRepository.findAllUserCurrentBookings(user1.getId(), PageRequest.of(0, 10)))
                .thenReturn(bookings);
        Mockito.when(itemMapper.toReturnDto(item1, new ArrayList<>(), new ArrayList<>()))
                .thenReturn(item1ReturnDto);
        Mockito.when(itemMapper.toReturnDto(item2, new ArrayList<>(), new ArrayList<>()))
                .thenReturn(item2ReturnDto);

        Mockito.when(commentRepository.findAllByItemId(any()))
                .thenReturn(new ArrayList<>());
        Mockito.when(bookingRepository.findAllByItemId(any()))
                .thenReturn(new ArrayList<>());

        List<BookingToReturnDto> actualBookings = bookingService.getBookingsByState("CURRENT",
                user1.getId(), 0, 10);

        assertThat(actualBookings.size(), equalTo(2));
        assertTrue(actualBookings.containsAll(List.of(booking1ReturnDto, booking2ReturnDto)));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user1.getId());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllUserCurrentBookings(user1.getId(), PageRequest.of(0, 10));
        Mockito.verify(bookingRepository, Mockito.times(2))
                .findAllByItemId(any());
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(itemMapper, Mockito.times(1))
                .toReturnDto(item1, new ArrayList<>(), new ArrayList<>());
        Mockito.verify(itemMapper, Mockito.times(1))
                .toReturnDto(item2, new ArrayList<>(), new ArrayList<>());
        Mockito.verifyNoMoreInteractions(itemMapper);
        Mockito.verify(commentRepository, Mockito.times(2))
                .findAllByItemId(any());
        Mockito.verifyNoMoreInteractions(commentRepository);
    }

    @Test
    void getBookingsByState_getByPast() {
        Mockito.when(userRepository.getReferenceById(user1.getId()))
                .thenReturn(user1);
        List<Booking> bookings = List.of(booking1,booking2);
        Mockito.when(bookingRepository.findAllUserPastBookings(user1.getId(), PageRequest.of(0, 10)))
                .thenReturn(bookings);
        Mockito.when(itemMapper.toReturnDto(item1, new ArrayList<>(), new ArrayList<>()))
                .thenReturn(item1ReturnDto);
        Mockito.when(itemMapper.toReturnDto(item2, new ArrayList<>(), new ArrayList<>()))
                .thenReturn(item2ReturnDto);

        Mockito.when(commentRepository.findAllByItemId(any()))
                .thenReturn(new ArrayList<>());
        Mockito.when(bookingRepository.findAllByItemId(any()))
                .thenReturn(new ArrayList<>());

        List<BookingToReturnDto> actualBookings = bookingService.getBookingsByState("PAST",
                user1.getId(), 0, 10);

        assertThat(actualBookings.size(), equalTo(2));
        assertTrue(actualBookings.containsAll(List.of(booking1ReturnDto, booking2ReturnDto)));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user1.getId());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllUserPastBookings(user1.getId(), PageRequest.of(0, 10));
        Mockito.verify(bookingRepository, Mockito.times(2))
                .findAllByItemId(any());
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(itemMapper, Mockito.times(1))
                .toReturnDto(item1, new ArrayList<>(), new ArrayList<>());
        Mockito.verify(itemMapper, Mockito.times(1))
                .toReturnDto(item2, new ArrayList<>(), new ArrayList<>());
        Mockito.verifyNoMoreInteractions(itemMapper);
        Mockito.verify(commentRepository, Mockito.times(2))
                .findAllByItemId(any());
        Mockito.verifyNoMoreInteractions(commentRepository);
    }

    @Test
    void getBookingsByState_getByFuture() {
        Mockito.when(userRepository.getReferenceById(user1.getId()))
                .thenReturn(user1);
        List<Booking> bookings = List.of(booking1,booking2);
        Mockito.when(bookingRepository.findAllUserFutureBookings(user1.getId(), PageRequest.of(0, 10)))
                .thenReturn(bookings);
        Mockito.when(itemMapper.toReturnDto(item1, new ArrayList<>(), new ArrayList<>()))
                .thenReturn(item1ReturnDto);
        Mockito.when(itemMapper.toReturnDto(item2, new ArrayList<>(), new ArrayList<>()))
                .thenReturn(item2ReturnDto);

        Mockito.when(commentRepository.findAllByItemId(any()))
                .thenReturn(new ArrayList<>());
        Mockito.when(bookingRepository.findAllByItemId(any()))
                .thenReturn(new ArrayList<>());

        List<BookingToReturnDto> actualBookings = bookingService.getBookingsByState("FUTURE",
                user1.getId(), 0, 10);

        assertThat(actualBookings.size(), equalTo(2));
        assertTrue(actualBookings.containsAll(List.of(booking1ReturnDto, booking2ReturnDto)));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user1.getId());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllUserFutureBookings(user1.getId(), PageRequest.of(0, 10));
        Mockito.verify(bookingRepository, Mockito.times(2))
                .findAllByItemId(any());
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(itemMapper, Mockito.times(1))
                .toReturnDto(item1, new ArrayList<>(), new ArrayList<>());
        Mockito.verify(itemMapper, Mockito.times(1))
                .toReturnDto(item2, new ArrayList<>(), new ArrayList<>());
        Mockito.verifyNoMoreInteractions(itemMapper);
        Mockito.verify(commentRepository, Mockito.times(2))
                .findAllByItemId(any());
        Mockito.verifyNoMoreInteractions(commentRepository);
    }

    @Test
    void getBookingsByState_getByWaiting() {
        Mockito.when(userRepository.getReferenceById(user1.getId()))
                .thenReturn(user1);
        List<Booking> bookings = List.of(booking1,booking2);
        Mockito.when(bookingRepository.findAllUserWaitingBookings(user1.getId(), PageRequest.of(0, 10)))
                .thenReturn(bookings);
        Mockito.when(itemMapper.toReturnDto(item1, new ArrayList<>(), new ArrayList<>()))
                .thenReturn(item1ReturnDto);
        Mockito.when(itemMapper.toReturnDto(item2, new ArrayList<>(), new ArrayList<>()))
                .thenReturn(item2ReturnDto);

        Mockito.when(commentRepository.findAllByItemId(any()))
                .thenReturn(new ArrayList<>());
        Mockito.when(bookingRepository.findAllByItemId(any()))
                .thenReturn(new ArrayList<>());

        List<BookingToReturnDto> actualBookings = bookingService.getBookingsByState("WAITING",
                user1.getId(), 0, 10);

        assertThat(actualBookings.size(), equalTo(2));
        assertTrue(actualBookings.containsAll(List.of(booking1ReturnDto, booking2ReturnDto)));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user1.getId());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllUserWaitingBookings(user1.getId(), PageRequest.of(0, 10));
        Mockito.verify(bookingRepository, Mockito.times(2))
                .findAllByItemId(any());
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(itemMapper, Mockito.times(1))
                .toReturnDto(item1, new ArrayList<>(), new ArrayList<>());
        Mockito.verify(itemMapper, Mockito.times(1))
                .toReturnDto(item2, new ArrayList<>(), new ArrayList<>());
        Mockito.verifyNoMoreInteractions(itemMapper);
        Mockito.verify(commentRepository, Mockito.times(2))
                .findAllByItemId(any());
        Mockito.verifyNoMoreInteractions(commentRepository);
    }

    @Test
    void getBookingsByState_getByRejected() {
        Mockito.when(userRepository.getReferenceById(user1.getId()))
                .thenReturn(user1);
        List<Booking> bookings = List.of(booking1,booking2);
        Mockito.when(bookingRepository.findAllUserRejectedBookings(user1.getId(), PageRequest.of(0, 10)))
                .thenReturn(bookings);
        Mockito.when(itemMapper.toReturnDto(item1, new ArrayList<>(), new ArrayList<>()))
                .thenReturn(item1ReturnDto);
        Mockito.when(itemMapper.toReturnDto(item2, new ArrayList<>(), new ArrayList<>()))
                .thenReturn(item2ReturnDto);

        Mockito.when(commentRepository.findAllByItemId(any()))
                .thenReturn(new ArrayList<>());
        Mockito.when(bookingRepository.findAllByItemId(any()))
                .thenReturn(new ArrayList<>());

        List<BookingToReturnDto> actualBookings = bookingService.getBookingsByState("REJECTED",
                user1.getId(), 0, 10);

        assertThat(actualBookings.size(), equalTo(2));
        assertTrue(actualBookings.containsAll(List.of(booking1ReturnDto, booking2ReturnDto)));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user1.getId());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllUserRejectedBookings(user1.getId(), PageRequest.of(0, 10));
        Mockito.verify(bookingRepository, Mockito.times(2))
                .findAllByItemId(any());
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(itemMapper, Mockito.times(1))
                .toReturnDto(item1, new ArrayList<>(), new ArrayList<>());
        Mockito.verify(itemMapper, Mockito.times(1))
                .toReturnDto(item2, new ArrayList<>(), new ArrayList<>());
        Mockito.verifyNoMoreInteractions(itemMapper);
        Mockito.verify(commentRepository, Mockito.times(2))
                .findAllByItemId(any());
        Mockito.verifyNoMoreInteractions(commentRepository);
    }

    @Test
    void getBookingsByState_getByAll() {
        Mockito.when(userRepository.getReferenceById(user1.getId()))
                .thenReturn(user1);
        List<Booking> bookings = List.of(booking1,booking2);
        Mockito.when(bookingRepository.findAllUserBookings(user1.getId(), PageRequest.of(0, 10)))
                .thenReturn(bookings);
        Mockito.when(itemMapper.toReturnDto(item1, new ArrayList<>(), new ArrayList<>()))
                .thenReturn(item1ReturnDto);
        Mockito.when(itemMapper.toReturnDto(item2, new ArrayList<>(), new ArrayList<>()))
                .thenReturn(item2ReturnDto);

        Mockito.when(commentRepository.findAllByItemId(any()))
                .thenReturn(new ArrayList<>());
        Mockito.when(bookingRepository.findAllByItemId(any()))
                .thenReturn(new ArrayList<>());

        List<BookingToReturnDto> actualBookings = bookingService.getBookingsByState("ALL",
                user1.getId(), 0, 10);

        assertThat(actualBookings.size(), equalTo(2));
        assertTrue(actualBookings.containsAll(List.of(booking1ReturnDto, booking2ReturnDto)));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user1.getId());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllUserBookings(user1.getId(), PageRequest.of(0, 10));
        Mockito.verify(bookingRepository, Mockito.times(2))
                .findAllByItemId(any());
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(itemMapper, Mockito.times(1))
                .toReturnDto(item1, new ArrayList<>(), new ArrayList<>());
        Mockito.verify(itemMapper, Mockito.times(1))
                .toReturnDto(item2, new ArrayList<>(), new ArrayList<>());
        Mockito.verifyNoMoreInteractions(itemMapper);
        Mockito.verify(commentRepository, Mockito.times(2))
                .findAllByItemId(any());
        Mockito.verifyNoMoreInteractions(commentRepository);
    }

    @Test
    void getBookingsByState_shouldThrowIllegalStateException() {
        Mockito.when(userRepository.getReferenceById(user1.getId()))
                .thenReturn(user1);

        assertThrows(IllegalStateException.class, () ->
                bookingService.getBookingsByState("abc", user1.getId(), 0, 10));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user1.getId());
        Mockito.verifyNoMoreInteractions(userRepository);

        Mockito.verifyNoInteractions(bookingRepository);
        Mockito.verifyNoInteractions(itemMapper);
        Mockito.verifyNoInteractions(commentRepository);
    }

    @Test
    void getUserItemsBookingsByState_getByCurrent() {
        Mockito.when(userRepository.getReferenceById(user1.getId()))
                .thenReturn(user1);
        List<Booking> bookings = List.of(booking1,booking2, booking3);
        Mockito.when(bookingRepository.findAllUserItemsCurrentBookings(user1.getId(), PageRequest.of(0, 10)))
                .thenReturn(bookings);
        Mockito.when(itemMapper.toReturnDto(item1, new ArrayList<>(), new ArrayList<>()))
                .thenReturn(item1ReturnDto);
        Mockito.when(itemMapper.toReturnDto(item2, new ArrayList<>(), new ArrayList<>()))
                .thenReturn(item2ReturnDto);

        Mockito.when(commentRepository.findAllByItemId(any()))
                .thenReturn(new ArrayList<>());
        Mockito.when(bookingRepository.findAllByItemId(any()))
                .thenReturn(new ArrayList<>());

        List<BookingToReturnDto> actualBookings = bookingService.getUserItemsBookingsByState("CURRENT",
                user1.getId(), 0, 10);

        assertThat(actualBookings.size(), equalTo(3));
        assertTrue(actualBookings.containsAll(List.of(booking1ReturnDto, booking2ReturnDto, booking3ReturnDto)));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user1.getId());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllUserItemsCurrentBookings(user1.getId(), PageRequest.of(0, 10));
        Mockito.verify(bookingRepository, Mockito.times(3))
                .findAllByItemId(any());
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(itemMapper, Mockito.times(2))
                .toReturnDto(item1, new ArrayList<>(), new ArrayList<>());
        Mockito.verify(itemMapper, Mockito.times(1))
                .toReturnDto(item2, new ArrayList<>(), new ArrayList<>());
        Mockito.verifyNoMoreInteractions(itemMapper);
        Mockito.verify(commentRepository, Mockito.times(3))
                .findAllByItemId(any());
        Mockito.verifyNoMoreInteractions(commentRepository);
    }

    @Test
    void getUserItemsBookingsByState_getByCurrentWithFrom() {
        Mockito.when(userRepository.getReferenceById(user1.getId()))
                .thenReturn(user1);
        List<Booking> bookings = List.of(booking2, booking3);
        Mockito.when(bookingRepository.findAllUserItemsCurrentBookings(user1.getId(), PageRequest.of(0, 10)))
                .thenReturn(bookings);
        Mockito.when(itemMapper.toReturnDto(item1, new ArrayList<>(), new ArrayList<>()))
                .thenReturn(item1ReturnDto);
        Mockito.when(itemMapper.toReturnDto(item2, new ArrayList<>(), new ArrayList<>()))
                .thenReturn(item2ReturnDto);

        Mockito.when(commentRepository.findAllByItemId(any()))
                .thenReturn(new ArrayList<>());
        Mockito.when(bookingRepository.findAllByItemId(any()))
                .thenReturn(new ArrayList<>());

        List<BookingToReturnDto> actualBookings = bookingService.getUserItemsBookingsByState("CURRENT",
                user1.getId(), 1, 10);

        assertThat(actualBookings.size(), equalTo(2));
        assertTrue(actualBookings.containsAll(List.of(booking2ReturnDto, booking3ReturnDto)));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user1.getId());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllUserItemsCurrentBookings(user1.getId(), PageRequest.of(0, 10));
        Mockito.verify(bookingRepository, Mockito.times(2))
                .findAllByItemId(any());
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(itemMapper, Mockito.times(1))
                .toReturnDto(item1, new ArrayList<>(), new ArrayList<>());
        Mockito.verify(itemMapper, Mockito.times(1))
                .toReturnDto(item2, new ArrayList<>(), new ArrayList<>());
        Mockito.verifyNoMoreInteractions(itemMapper);
        Mockito.verify(commentRepository, Mockito.times(2))
                .findAllByItemId(any());
        Mockito.verifyNoMoreInteractions(commentRepository);
    }

    @Test
    void getUserItemsBookingsByState_getByCurrentWithBigFrom() {
        Mockito.when(userRepository.getReferenceById(user1.getId()))
                .thenReturn(user1);
        Mockito.when(bookingRepository.findAllUserItemsCurrentBookings(user1.getId(), PageRequest.of(1, 10)))
                .thenReturn(new ArrayList<>());
        Mockito.when(itemMapper.toReturnDto(item1, new ArrayList<>(), new ArrayList<>()))
                .thenReturn(item1ReturnDto);
        Mockito.when(itemMapper.toReturnDto(item2, new ArrayList<>(), new ArrayList<>()))
                .thenReturn(item2ReturnDto);

        Mockito.when(commentRepository.findAllByItemId(any()))
                .thenReturn(new ArrayList<>());
        Mockito.when(bookingRepository.findAllByItemId(any()))
                .thenReturn(new ArrayList<>());

        List<BookingToReturnDto> actualBookings = bookingService.getUserItemsBookingsByState("CURRENT",
                user1.getId(), 11, 10);

        assertNotNull(actualBookings);
        assertThat(actualBookings.size(), equalTo(0));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user1.getId());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllUserItemsCurrentBookings(user1.getId(), PageRequest.of(1, 10));
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verifyNoInteractions(itemMapper);
        Mockito.verifyNoInteractions(commentRepository);
    }

    @Test
    void getUserItemsBookingsByState_getByCurrentWithSize() {
        Mockito.when(userRepository.getReferenceById(user1.getId()))
                .thenReturn(user1);
        List<Booking> bookings = List.of(booking1,booking2);
        Mockito.when(bookingRepository.findAllUserItemsCurrentBookings(user1.getId(), PageRequest.of(0, 2)))
                .thenReturn(bookings);
        Mockito.when(itemMapper.toReturnDto(item1, new ArrayList<>(), new ArrayList<>()))
                .thenReturn(item1ReturnDto);
        Mockito.when(itemMapper.toReturnDto(item2, new ArrayList<>(), new ArrayList<>()))
                .thenReturn(item2ReturnDto);

        Mockito.when(commentRepository.findAllByItemId(any()))
                .thenReturn(new ArrayList<>());
        Mockito.when(bookingRepository.findAllByItemId(any()))
                .thenReturn(new ArrayList<>());

        List<BookingToReturnDto> actualBookings = bookingService.getUserItemsBookingsByState("CURRENT",
                user1.getId(), 0, 2);

        assertThat(actualBookings.size(), equalTo(2));
        assertTrue(actualBookings.containsAll(List.of(booking1ReturnDto, booking2ReturnDto)));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user1.getId());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllUserItemsCurrentBookings(user1.getId(), PageRequest.of(0, 2));
        Mockito.verify(bookingRepository, Mockito.times(2))
                .findAllByItemId(any());
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(itemMapper, Mockito.times(1))
                .toReturnDto(item1, new ArrayList<>(), new ArrayList<>());
        Mockito.verify(itemMapper, Mockito.times(1))
                .toReturnDto(item2, new ArrayList<>(), new ArrayList<>());
        Mockito.verifyNoMoreInteractions(itemMapper);
        Mockito.verify(commentRepository, Mockito.times(2))
                .findAllByItemId(any());
        Mockito.verifyNoMoreInteractions(commentRepository);
    }

    @Test
    void getUserItemsBookingsByState_getByPast() {
        Mockito.when(userRepository.getReferenceById(user1.getId()))
                .thenReturn(user1);
        List<Booking> bookings = List.of(booking1,booking2, booking3);
        Mockito.when(bookingRepository.findAllUserItemsPastBookings(user1.getId(), PageRequest.of(0, 10)))
                .thenReturn(bookings);
        Mockito.when(itemMapper.toReturnDto(item1, new ArrayList<>(), new ArrayList<>()))
                .thenReturn(item1ReturnDto);
        Mockito.when(itemMapper.toReturnDto(item2, new ArrayList<>(), new ArrayList<>()))
                .thenReturn(item2ReturnDto);

        Mockito.when(commentRepository.findAllByItemId(any()))
                .thenReturn(new ArrayList<>());
        Mockito.when(bookingRepository.findAllByItemId(any()))
                .thenReturn(new ArrayList<>());

        List<BookingToReturnDto> actualBookings = bookingService.getUserItemsBookingsByState("PAST",
                user1.getId(), 0, 10);

        assertThat(actualBookings.size(), equalTo(3));
        assertTrue(actualBookings.containsAll(List.of(booking1ReturnDto, booking2ReturnDto, booking3ReturnDto)));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user1.getId());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllUserItemsPastBookings(user1.getId(), PageRequest.of(0, 10));
        Mockito.verify(bookingRepository, Mockito.times(3))
                .findAllByItemId(any());
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(itemMapper, Mockito.times(2))
                .toReturnDto(item1, new ArrayList<>(), new ArrayList<>());
        Mockito.verify(itemMapper, Mockito.times(1))
                .toReturnDto(item2, new ArrayList<>(), new ArrayList<>());
        Mockito.verifyNoMoreInteractions(itemMapper);
        Mockito.verify(commentRepository, Mockito.times(3))
                .findAllByItemId(any());
        Mockito.verifyNoMoreInteractions(commentRepository);
    }

    @Test
    void getUserItemsBookingsByState_getByFuture() {
        Mockito.when(userRepository.getReferenceById(user1.getId()))
                .thenReturn(user1);
        List<Booking> bookings = List.of(booking1,booking2, booking3);
        Mockito.when(bookingRepository.findAllUserItemsFutureBookings(user1.getId(), PageRequest.of(0, 10)))
                .thenReturn(bookings);
        Mockito.when(itemMapper.toReturnDto(item1, new ArrayList<>(), new ArrayList<>()))
                .thenReturn(item1ReturnDto);
        Mockito.when(itemMapper.toReturnDto(item2, new ArrayList<>(), new ArrayList<>()))
                .thenReturn(item2ReturnDto);

        Mockito.when(commentRepository.findAllByItemId(any()))
                .thenReturn(new ArrayList<>());
        Mockito.when(bookingRepository.findAllByItemId(any()))
                .thenReturn(new ArrayList<>());

        List<BookingToReturnDto> actualBookings = bookingService.getUserItemsBookingsByState("FUTURE",
                user1.getId(), 0, 10);

        assertThat(actualBookings.size(), equalTo(3));
        assertTrue(actualBookings.containsAll(List.of(booking1ReturnDto, booking2ReturnDto, booking3ReturnDto)));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user1.getId());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllUserItemsFutureBookings(user1.getId(), PageRequest.of(0, 10));
        Mockito.verify(bookingRepository, Mockito.times(3))
                .findAllByItemId(any());
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(itemMapper, Mockito.times(2))
                .toReturnDto(item1, new ArrayList<>(), new ArrayList<>());
        Mockito.verify(itemMapper, Mockito.times(1))
                .toReturnDto(item2, new ArrayList<>(), new ArrayList<>());
        Mockito.verifyNoMoreInteractions(itemMapper);
        Mockito.verify(commentRepository, Mockito.times(3))
                .findAllByItemId(any());
        Mockito.verifyNoMoreInteractions(commentRepository);
    }

    @Test
    void getUserItemsBookingsByState_getByWaiting() {
        Mockito.when(userRepository.getReferenceById(user1.getId()))
                .thenReturn(user1);
        List<Booking> bookings = List.of(booking1,booking2, booking3);
        Mockito.when(bookingRepository.findAllUserItemsWaitingBookings(user1.getId(), PageRequest.of(0, 10)))
                .thenReturn(bookings);
        Mockito.when(itemMapper.toReturnDto(item1, new ArrayList<>(), new ArrayList<>()))
                .thenReturn(item1ReturnDto);
        Mockito.when(itemMapper.toReturnDto(item2, new ArrayList<>(), new ArrayList<>()))
                .thenReturn(item2ReturnDto);

        Mockito.when(commentRepository.findAllByItemId(any()))
                .thenReturn(new ArrayList<>());
        Mockito.when(bookingRepository.findAllByItemId(any()))
                .thenReturn(new ArrayList<>());

        List<BookingToReturnDto> actualBookings = bookingService.getUserItemsBookingsByState("WAITING",
                user1.getId(), 0, 10);

        assertThat(actualBookings.size(), equalTo(3));
        assertTrue(actualBookings.containsAll(List.of(booking1ReturnDto, booking2ReturnDto, booking3ReturnDto)));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user1.getId());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllUserItemsWaitingBookings(user1.getId(), PageRequest.of(0, 10));
        Mockito.verify(bookingRepository, Mockito.times(3))
                .findAllByItemId(any());
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(itemMapper, Mockito.times(2))
                .toReturnDto(item1, new ArrayList<>(), new ArrayList<>());
        Mockito.verify(itemMapper, Mockito.times(1))
                .toReturnDto(item2, new ArrayList<>(), new ArrayList<>());
        Mockito.verifyNoMoreInteractions(itemMapper);
        Mockito.verify(commentRepository, Mockito.times(3))
                .findAllByItemId(any());
        Mockito.verifyNoMoreInteractions(commentRepository);
    }

    @Test
    void getUserItemsBookingsByState_getByRejected() {
        Mockito.when(userRepository.getReferenceById(user1.getId()))
                .thenReturn(user1);
        List<Booking> bookings = List.of(booking1,booking2, booking3);
        Mockito.when(bookingRepository.findAllUserItemsRejectedBookings(user1.getId(), PageRequest.of(0, 10)))
                .thenReturn(bookings);
        Mockito.when(itemMapper.toReturnDto(item1, new ArrayList<>(), new ArrayList<>()))
                .thenReturn(item1ReturnDto);
        Mockito.when(itemMapper.toReturnDto(item2, new ArrayList<>(), new ArrayList<>()))
                .thenReturn(item2ReturnDto);

        Mockito.when(commentRepository.findAllByItemId(any()))
                .thenReturn(new ArrayList<>());
        Mockito.when(bookingRepository.findAllByItemId(any()))
                .thenReturn(new ArrayList<>());

        List<BookingToReturnDto> actualBookings = bookingService.getUserItemsBookingsByState("REJECTED",
                user1.getId(), 0, 10);

        assertThat(actualBookings.size(), equalTo(3));
        assertTrue(actualBookings.containsAll(List.of(booking1ReturnDto, booking2ReturnDto, booking3ReturnDto)));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user1.getId());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllUserItemsRejectedBookings(user1.getId(), PageRequest.of(0, 10));
        Mockito.verify(bookingRepository, Mockito.times(3))
                .findAllByItemId(any());
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(itemMapper, Mockito.times(2))
                .toReturnDto(item1, new ArrayList<>(), new ArrayList<>());
        Mockito.verify(itemMapper, Mockito.times(1))
                .toReturnDto(item2, new ArrayList<>(), new ArrayList<>());
        Mockito.verifyNoMoreInteractions(itemMapper);
        Mockito.verify(commentRepository, Mockito.times(3))
                .findAllByItemId(any());
        Mockito.verifyNoMoreInteractions(commentRepository);
    }

    @Test
    void getUserItemsBookingsByState_getByAll() {
        Mockito.when(userRepository.getReferenceById(user1.getId()))
                .thenReturn(user1);
        List<Booking> bookings = List.of(booking1,booking2, booking3);
        Mockito.when(bookingRepository.findAllUserItemsBookings(user1.getId(), PageRequest.of(0, 10)))
                .thenReturn(bookings);
        Mockito.when(itemMapper.toReturnDto(item1, new ArrayList<>(), new ArrayList<>()))
                .thenReturn(item1ReturnDto);
        Mockito.when(itemMapper.toReturnDto(item2, new ArrayList<>(), new ArrayList<>()))
                .thenReturn(item2ReturnDto);

        Mockito.when(commentRepository.findAllByItemId(any()))
                .thenReturn(new ArrayList<>());
        Mockito.when(bookingRepository.findAllByItemId(any()))
                .thenReturn(new ArrayList<>());

        List<BookingToReturnDto> actualBookings = bookingService.getUserItemsBookingsByState("ALL",
                user1.getId(), 0, 10);

        assertThat(actualBookings.size(), equalTo(3));
        assertTrue(actualBookings.containsAll(List.of(booking1ReturnDto, booking2ReturnDto, booking3ReturnDto)));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user1.getId());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllUserItemsBookings(user1.getId(), PageRequest.of(0, 10));
        Mockito.verify(bookingRepository, Mockito.times(3))
                .findAllByItemId(any());
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(itemMapper, Mockito.times(2))
                .toReturnDto(item1, new ArrayList<>(), new ArrayList<>());
        Mockito.verify(itemMapper, Mockito.times(1))
                .toReturnDto(item2, new ArrayList<>(), new ArrayList<>());
        Mockito.verifyNoMoreInteractions(itemMapper);
        Mockito.verify(commentRepository, Mockito.times(3))
                .findAllByItemId(any());
        Mockito.verifyNoMoreInteractions(commentRepository);
    }

    @Test
    void update_withNotNullBooking() {
        Mockito.when(userRepository.getReferenceById(user2.getId()))
                .thenReturn(user2);
        Mockito.when(bookingRepository.getReferenceById(booking1.getId()))
                .thenReturn(booking1);
        Mockito.when(bookingRepository.findAllByItemId(booking1.getItem().getId()))
                .thenReturn(new ArrayList<>());
        Mockito.when(bookingRepository.save(booking4))
                .thenReturn(booking4);
        Mockito.when(commentRepository.findAllByItemId(booking1.getItem().getId()))
                .thenReturn(new ArrayList<>());
        Mockito.when(itemMapper.toReturnDto(booking1.getItem(), new ArrayList<>(), new ArrayList<>()))
                .thenReturn(item1ReturnDto);

        BookingToReturnDto updatedBooking = bookingService.update(booking4GetDto, user2.getId(), booking1.getId(), null);

        assertThat(updatedBooking, equalTo(booking4ReturnDto));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user2.getId());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .getReferenceById(booking1.getId());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemId(booking1.getItem().getId());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .save(booking4);
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(commentRepository, Mockito.times(1))
                .findAllByItemId(booking1.getItem().getId());
        Mockito.verifyNoMoreInteractions(commentRepository);
        Mockito.verify(itemMapper, Mockito.times(1))
                .toReturnDto(booking1.getItem(), new ArrayList<>(), new ArrayList<>());
        Mockito.verifyNoMoreInteractions(itemMapper);
    }

    @Test
    void update_shouldThrowInvalidIdException() {
        Mockito.when(userRepository.getReferenceById(user2.getId()))
                .thenReturn(user2);
        Mockito.when(bookingRepository.getReferenceById(booking1.getId()))
                .thenReturn(booking1);
        booking4GetDto.setId(0L);
        assertThrows(InvalidIdException.class, () ->
                bookingService.update(booking4GetDto, user2.getId(), booking4.getId(), null));
        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user2.getId());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .getReferenceById(booking1.getId());
        Mockito.verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void update_shouldUpdateWithTrueApproved() {
        Mockito.when(userRepository.getReferenceById(user1.getId()))
                .thenReturn(user1);
        Mockito.when(bookingRepository.getReferenceById(booking1.getId()))
                .thenReturn(booking1);
        Mockito.when(bookingRepository.findAllByItemId(booking1.getItem().getId()))
                .thenReturn(new ArrayList<>());
        Mockito.when(bookingRepository.save(booking1))
                .thenReturn(booking1);
        Mockito.when(commentRepository.findAllByItemId(booking1.getItem().getId()))
                .thenReturn(new ArrayList<>());
        Mockito.when(itemMapper.toReturnDto(booking1.getItem(), new ArrayList<>(), new ArrayList<>()))
                .thenReturn(item1ReturnDto);

        BookingToReturnDto updatedBooking = bookingService.update(null, user1.getId(), booking1.getId(), true);
        booking1ReturnDto.setStatus(Status.APPROVED);
        assertThat(updatedBooking, equalTo(booking1ReturnDto));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user1.getId());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .getReferenceById(booking1.getId());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findAllByItemId(booking1.getItem().getId());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .save(booking1);
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verify(commentRepository, Mockito.times(1))
                .findAllByItemId(booking1.getItem().getId());
        Mockito.verifyNoMoreInteractions(commentRepository);
        Mockito.verify(itemMapper, Mockito.times(1))
                .toReturnDto(booking1.getItem(), new ArrayList<>(), new ArrayList<>());
        Mockito.verifyNoMoreInteractions(itemMapper);
    }

    @Test
    void update_withNotNullBooking_shouldThrowNoPermissionException() {
        Mockito.when(userRepository.getReferenceById(user1.getId()))
                .thenReturn(user1);
        Mockito.when(bookingRepository.getReferenceById(booking1.getId()))
                .thenReturn(booking1);

        assertThrows(NoPermissionException.class, () ->
                bookingService.update(booking4GetDto, user1.getId(), booking1.getId(), null));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user1.getId());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .getReferenceById(booking1.getId());
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verifyNoInteractions(commentRepository);
        Mockito.verifyNoInteractions(itemMapper);
    }

    @Test
    void update_withTrueApproved_shouldThrowNoPermissionException() {
        Mockito.when(userRepository.getReferenceById(user2.getId()))
                .thenReturn(user2);
        Mockito.when(bookingRepository.getReferenceById(booking1.getId()))
                .thenReturn(booking1);
        assertThrows(NoPermissionException.class, () ->
                bookingService.update(null, user2.getId(), booking1.getId(), true));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user2.getId());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .getReferenceById(booking1.getId());
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verifyNoInteractions(commentRepository);
        Mockito.verifyNoInteractions(itemMapper);
    }

    @Test
    void update_withTrueApproved_shouldThrowIllegalStatusException() {
        booking4.setStatus(Status.APPROVED);
        booking4ReturnDto.setStatus(Status.APPROVED);
        Mockito.when(userRepository.getReferenceById(user1.getId()))
                .thenReturn(user1);
        Mockito.when(bookingRepository.getReferenceById(booking4.getId()))
                .thenReturn(booking4);
        assertThrows(IllegalStatusException.class, () ->
                bookingService.update(null, user1.getId(), booking4.getId(), true));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user1.getId());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .getReferenceById(booking1.getId());
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verifyNoInteractions(commentRepository);
        Mockito.verifyNoInteractions(itemMapper);
    }

    @Test
    void update_withFalseApproved_shouldThrowIllegalStatusException() {
        booking4.setStatus(Status.REJECTED);
        booking4ReturnDto.setStatus(Status.REJECTED);
        Mockito.when(userRepository.getReferenceById(user1.getId()))
                .thenReturn(user1);
        Mockito.when(bookingRepository.getReferenceById(booking4.getId()))
                .thenReturn(booking4);
        assertThrows(IllegalStatusException.class, () ->
                bookingService.update(null, user1.getId(), booking4.getId(), false));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user1.getId());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .getReferenceById(booking4.getId());
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verifyNoInteractions(commentRepository);
        Mockito.verifyNoInteractions(itemMapper);
    }

    @Test
    void update_shouldThrowUnavailableException() {
        Mockito.when(userRepository.getReferenceById(user1.getId()))
                .thenReturn(user1);
        Mockito.when(bookingRepository.getReferenceById(booking1.getId()))
                .thenReturn(booking1);
        assertThrows(UnavailableException.class, () ->
                bookingService.update(null, user1.getId(), booking1.getId(), null));

        Mockito.verify(userRepository, Mockito.times(1))
                .getReferenceById(user1.getId());
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .getReferenceById(booking1.getId());
        Mockito.verifyNoMoreInteractions(bookingRepository);
        Mockito.verifyNoInteractions(commentRepository);
        Mockito.verifyNoInteractions(itemMapper);
    }
}