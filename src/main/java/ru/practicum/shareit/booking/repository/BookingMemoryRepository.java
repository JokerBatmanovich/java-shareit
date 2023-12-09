package ru.practicum.shareit.booking.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BookingNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Component
public class BookingMemoryRepository implements BookingRepository {

    private final HashMap<Long, Booking> bookings = new HashMap<>();
    private Long idCounter = 1L;

    @Override
    public Booking getById(Long id) {
        if (bookings.containsKey(id)) {
            throw new BookingNotFoundException(id);
        }
        return (bookings.get(id));
    }

    @Override
    public List<Booking> getByItemId(Long itemId) {
        List<Booking> listToReturn = new ArrayList<>();
        bookings.values()
                .forEach(booking -> {
                    if (Objects.equals(booking.getItem().getId(), itemId)) {
                        listToReturn.add(booking);
                    }
                });
        return listToReturn;
    }

    @Override
    public List<Booking> getByUserId(Long userId) {
        List<Booking> listToReturn = new ArrayList<>();
        bookings.values()
                .forEach(booking -> {
                    if (Objects.equals(booking.getBooker().getId(), userId)) {
                        listToReturn.add(booking);
                    }
                });
        return listToReturn;
    }

    @Override
    public Booking add(Booking booking) {
        bookings.put(idCounter, booking);
        return getById(idCounter++);
    }

    @Override
    public Booking update(Booking booking) {
        bookings.put(booking.getId(), booking);
        return getById(booking.getId());
    }

    @Override
    public void deleteById(Long id) {
        bookings.remove(id);
    }

    @Override
    public void deleteByItemId(Long itemId) {
        bookings.values()
                .forEach(booking -> {
                    if (Objects.equals(booking.getItem().getId(), itemId)) {
                        bookings.remove(booking.getId());
                    }
                });
    }

    @Override
    public void deleteByUserId(Long userId) {
        bookings.values()
                .forEach(booking -> {
                    if (Objects.equals(booking.getBooker().getId(), userId)) {
                        bookings.remove(booking.getId());
                    }
                });
    }
}
