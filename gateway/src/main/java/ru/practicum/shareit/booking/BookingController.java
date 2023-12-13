package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.exception.IllegalStateException;
import ru.practicum.shareit.exception.InvalidBookingTimeException;
import ru.practicum.shareit.exception.InvalidIdException;

import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/bookings")
@Validated
@Slf4j
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                  @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
                                                  @RequestParam(name = "size", defaultValue = "10") @Min(1) Integer size) {
        return bookingClient.getUserBookings(userId, checkState(state), from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getUserItemsBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                       @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
                                                       @RequestParam(name = "size", defaultValue = "10") @Min(1) Integer size) {
        return bookingClient.getUserItemsBookings(userId, checkState(state), from, size);
    }

    @PostMapping()
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestBody @Validated({Create.class}) BookingToGetDto bookingToGetDto) {
        checkTimes(bookingToGetDto);
        return bookingClient.add(userId, bookingToGetDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long bookingId,
                                         @RequestParam(name = "approved", required = false) Boolean approved,
                                         @RequestBody(required = false) BookingToGetDto bookingToGetDto) {
        if (bookingToGetDto != null) {
            if (bookingToGetDto.getId() != null && !bookingToGetDto.getId().equals(bookingId)) {
                throw new InvalidIdException("ID тела запроса не совпадает с ID из параметров.");
            }
            if (bookingToGetDto.getStart() != null && bookingToGetDto.getEnd() != null) {
                checkTimes(bookingToGetDto);
            }
        }
        return bookingClient.update(userId, bookingId, bookingToGetDto, approved);
    }

    private void checkTimes(BookingToGetDto booking) {
        if (!booking.getStart().isBefore(booking.getEnd())) {
            throw new InvalidBookingTimeException("Время начала бронирования должно быть перед временем окончания.");
        }
    }

    private State checkState(String state) {
        try {
            return State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(state);
        }
    }

}
