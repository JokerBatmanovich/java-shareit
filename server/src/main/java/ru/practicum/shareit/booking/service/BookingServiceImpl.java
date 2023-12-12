package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

        return bookingMapper.toReturnDto(booking,itemBookings,itemComments);
    }

    @Override
    public List<BookingToReturnDto> getBookingsByState(State state, Long userId, Integer from, Integer size) {
        checkUserExistence(userId);
        List<Booking> bookings;
        Pageable page = PageRequest.of(from / size, size);
        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findAllUserCurrentBookings(userId, page);
                break;
            case PAST:
                bookings = bookingRepository.findAllUserPastBookings(userId, page);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllUserFutureBookings(userId, page);
                break;
            case WAITING:
                bookings = bookingRepository.findAllUserWaitingBookings(userId, page);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllUserRejectedBookings(userId, page);
                break;
            default:
                bookings = bookingRepository.findAllUserBookings(userId, page);
        }
        return toBookingToReturnDtoList(bookings);
    }

    @Override
    public List<BookingToReturnDto> getUserItemsBookingsByState(State state, Long userId, Integer from, Integer size) {
        checkUserExistence(userId);
        List<Booking> bookings;
        Pageable page = PageRequest.of(from / size, size);
        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findAllUserItemsCurrentBookings(userId, page);
                break;
            case PAST:
                bookings = bookingRepository.findAllUserItemsPastBookings(userId, page);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllUserItemsFutureBookings(userId, page);
                break;
            case WAITING:
                bookings = bookingRepository.findAllUserItemsWaitingBookings(userId, page);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllUserItemsRejectedBookings(userId, page);
                break;
            default:
                bookings = bookingRepository.findAllUserItemsBookings(userId, page);
        }
        return toBookingToReturnDtoList(bookings);
    }

    @Override
    public BookingToReturnDto add(BookingToGetDto bookingGetDto, Long userId, Long itemId) {
        User user = checkUserExistence(userId);
        Item item = checkAvailability(userId, itemId);
        Booking booking = bookingMapper.toEntity(bookingGetDto);
        booking.setStatus(Status.WAITING);
        booking.setBooker(user);
        booking.setItem(item);
        return bookingMapper.toReturnDto(bookingRepository.save(booking), new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public BookingToReturnDto update(BookingToGetDto newBooking, Long userId, Long bookingId, Boolean approved) {
        checkUserExistence(userId);
        Booking oldBooking = checkBookingExistence(bookingId);
        if (newBooking != null) {
            checkBookerPermissions(userId, oldBooking);
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
        } else {
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
        }
        List<Comment> comments = commentRepository.findAllByItemId(oldBooking.getItem().getId());
        List<Booking> bookings = bookingRepository.findAllByItemId(oldBooking.getItem().getId());
        return bookingMapper.toReturnDto(bookingRepository.save(oldBooking), bookings, comments);
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
            bookingToReturnDto.add(bookingMapper.toReturnDto(booking, itemBookings, itemComments));
        }
        return bookingToReturnDto;
    }


}
