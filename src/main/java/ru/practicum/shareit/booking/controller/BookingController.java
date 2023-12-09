package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.booking.dto.BookingToReturnDto;
import ru.practicum.shareit.booking.dto.BookingToGetDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Validated
@RequiredArgsConstructor
public class BookingController {
    @Qualifier("bookingServiceImpl")
    final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingToReturnDto get(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingToReturnDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.getBookingsByState(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingToReturnDto> getUserItemsBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.getUserItemsBookingsByState(state, userId);
    }

    @PostMapping()
    public BookingToReturnDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @RequestBody @Validated({Create.class}) BookingToGetDto bookingToGetDto) {
        return bookingService.add(bookingToGetDto, userId, bookingToGetDto.getItemId());
    }

    @PatchMapping("/{bookingId}")
    public BookingToReturnDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId,
                                     @RequestParam(name = "approved", required = false) Boolean approved,
                                     @RequestBody(required = false) BookingToGetDto bookingToGetDto) {
        return bookingService.update(bookingToGetDto, userId, bookingId, approved);
    }
}
