package ru.practicum.shareit.booking.repository;

import ru.practicum.shareit.booking.Booking;

import java.util.List;

public interface BookingRepository {
    Booking getById(Long id);

    List<Booking> getByItemId(Long itemId);

    List<Booking> getByUserId(Long userId);

    Booking add(Booking booking);

    Booking update(Booking booking);

    void deleteById(Long id);

    void deleteByItemId(Long itemId);

    void deleteByUserId(Long userId);

}
