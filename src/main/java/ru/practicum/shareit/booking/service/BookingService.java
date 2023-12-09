package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingToGetDto;
import ru.practicum.shareit.booking.dto.BookingToReturnDto;

import java.util.List;

public interface BookingService {

    BookingToReturnDto getById(Long bookingId, Long userid);

    List<BookingToReturnDto> getBookingsByState(String state, Long userId);

    List<BookingToReturnDto> getUserItemsBookingsByState(String state, Long userId);

    BookingToReturnDto add(BookingToGetDto bookingGetDto, Long userId, Long itemId);

    BookingToReturnDto update(BookingToGetDto bookingGetDto, Long userId, Long bookingId, Boolean approved);

    void deleteById(Long bookingId, Long userId);
}
