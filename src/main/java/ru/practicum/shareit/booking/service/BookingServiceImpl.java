package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingToGetDto;
import ru.practicum.shareit.booking.dto.BookingToReturnDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.JpaCommentRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.exception.IllegalStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    final JpaItemRepository itemRepository;
    final JpaUserRepository userRepository;
    final JpaBookingRepository bookingRepository;
    final JpaCommentRepository commentRepository;
    final BookingMapper bookingMapper;


    @Override
    public BookingToReturnDto getById(Long bookingId, Long userId) {
        checkUserExistence(userId);
        Booking booking = checkBookingExistence(bookingId);
        checkUserPermissions(userId, booking);
        List<Booking> itemBookings = bookingRepository.findAllByItemId(booking.getItem().getId());
        List<Comment> itemComments = commentRepository.findAllByItemId(booking.getItem().getId());

        return bookingMapper.toBookingReturnDto(booking,itemBookings,itemComments);
    }

    @Override
    public List<BookingToReturnDto> getBookingsByState(String state, Long userId) {
        checkUserExistence(userId);
        checkState(state);
        List<Booking> bookings;
        switch (state) {
            case "CURRENT":
                bookings = bookingRepository.findAllUserCurrentBookings(userId);
                break;
            case "PAST":
                bookings = bookingRepository.findAllUserPastBookings(userId);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllUserFutureBookings(userId);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllUserWaitingBookings(userId);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllUserRejectedBookings(userId);
                break;
            default:
                bookings = bookingRepository.findAllUserBookings(userId);
        }
        return toBookingToReturnDtoList(bookings);
    }

    @Override
    public List<BookingToReturnDto> getUserItemsBookingsByState(String state, Long userId) {
        checkUserExistence(userId);
        checkState(state);
        List<Booking> bookings;
        switch (state) {
            case "CURRENT":
                bookings = bookingRepository.findAllUserItemsCurrentBookings(userId);
                break;
            case "PAST":
                bookings = bookingRepository.findAllUserItemsPastBookings(userId);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllUserItemsFutureBookings(userId);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllUserItemsWaitingBookings(userId);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllUserItemsRejectedBookings(userId);
                break;
            default:
                bookings = bookingRepository.findAllUserItemsBookings(userId);
        }
        return toBookingToReturnDtoList(bookings);
    }

    @Override
    public BookingToReturnDto add(BookingToGetDto bookingGetDto, Long userId, Long itemId) {
        User user = checkUserExistence(userId);
        checkTimes(bookingGetDto);
        Item item = checkAvailability(userId, itemId);
        Booking booking = bookingMapper.toBooking(bookingGetDto);
        booking.setStatus(Status.WAITING);
        booking.setBooker(user);
        booking.setItem(item);
        return bookingMapper.toBookingReturnDto(bookingRepository.save(booking), new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public BookingToReturnDto update(BookingToGetDto newBooking, Long userId, Long bookingId, Boolean approved) {
        checkUserExistence(userId);
        Booking oldBooking = checkBookingExistence(bookingId);
        if (newBooking != null) {
            checkBookerPermissions(userId, oldBooking);
            if (newBooking.getId() != null && !newBooking.getId().equals(bookingId)) {
                throw new InvalidIdException("ID тела запроса не совпадает с ID из параметров.");
            }
            if (newBooking.getStart() != null && newBooking.getEnd() != null) {
                checkTimes(newBooking);
            }
            if (newBooking.getItemId() != null && !newBooking.getItemId().equals(oldBooking.getItem().getId())) {
                Item item = checkAvailability(userId, newBooking.getItemId());
                oldBooking.setItem(item);
                checkBookerPermissions(userId, oldBooking);
            }
            if (newBooking.getStart() != null) {
                oldBooking.setStart(newBooking.getStart());
            }
            if (newBooking.getEnd() != null) {
                oldBooking.setEnd(newBooking.getEnd());
            }
        }
        if (approved != null) {
            checkOwnerPermissions(userId, oldBooking);
            if (approved) {
                if (!oldBooking.getStatus().equals(Status.APPROVED)) {
                    oldBooking.setStatus(Status.APPROVED);
                } else {
                    throw new IllegalStatusException("Нельзя поменять статус на такой же.");
                }
            } else {
                if (!oldBooking.getStatus().equals(Status.REJECTED)) {
                    oldBooking.setStatus(Status.REJECTED);
                } else {
                    throw new IllegalStatusException("Нельзя поменять статус на такой же.");
                }
            }
        } else {
            throw new UnavailableException();
        }
        List<Comment> comments = commentRepository.findAllByItemId(oldBooking.getItem().getId());
        List<Booking> bookings = bookingRepository.findAllByItemId(oldBooking.getItem().getId());
        return bookingMapper.toBookingReturnDto(bookingRepository.save(oldBooking), bookings, comments);
    }

    @Override
    public void deleteById(Long bookingId, Long userId) {
        bookingRepository.deleteById(bookingId);
    }

    private void checkOwnerPermissions(Long ownerId, Booking booking) {
        if (!ownerId.equals(booking.getItem().getOwner().getId())) {
            throw new NoPermissionException(ownerId);
        }
    }

    private void checkBookerPermissions(Long bookerId, Booking booking) {
        if (!bookerId.equals(booking.getBooker().getId())) {
            throw new NoPermissionException(bookerId);
        }
    }

    private void checkUserPermissions(Long userId, Booking booking) {
        if (!userId.equals(booking.getItem().getOwner().getId())
                && !userId.equals(booking.getBooker().getId())) {
            throw new NoPermissionException(userId);
        }
    }

    private User checkUserExistence(Long userId) {
        try {
            User user = userRepository.getReferenceById(userId);
            System.out.println(user);
            return user;
        } catch (EntityNotFoundException e) {
            throw new UserNotFoundException(userId);
        }
    }

    private Booking checkBookingExistence(Long bookingId) {
        try {
            Booking booking = bookingRepository.getReferenceById(bookingId);
            System.out.println(booking);
            return booking;
        } catch (EntityNotFoundException e) {
            throw new BookingNotFoundException(bookingId);
        }
    }

    private void checkTimes(BookingToGetDto booking) {
        if (!booking.getStart().isBefore(booking.getEnd())) {
            throw new InvalidBookingTimeException("Время начала бронирования должно быть перед временем окончания.");
        }
    }

    private void checkState(String state) {
        try {
            State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(state);
        }
    }

    private Item checkAvailability(Long userId, Long itemId) {
        Item item;
        try {
            item = itemRepository.getReferenceById(itemId);
            System.out.println(item);
        } catch (EntityNotFoundException e) {
            throw new ItemNotFoundException(itemId);
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new NoPermissionException(itemId);
        }
        if (!(item.getAvailable())) {
            throw new UnavailableException(itemId);
        }
        return item;
    }

    private List<BookingToReturnDto> toBookingToReturnDtoList(List<Booking> bookings) {
        List<BookingToReturnDto> bookingToReturnDto = new ArrayList<>();
        List<Comment> itemComments;
        List<Booking> itemBookings;
        for (Booking booking : bookings) {
            itemComments = commentRepository.findAllByItemId(booking.getItem().getId());
            itemBookings = bookingRepository.findAllByItemId(booking.getItem().getId());
            bookingToReturnDto.add(bookingMapper.toBookingReturnDto(booking, itemBookings, itemComments));
        }
        return bookingToReturnDto;
    }


}
