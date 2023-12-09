package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class BookingTest {
    @Test
    void compareToTest() {
        Booking booking1 = Booking.builder()
                .start(LocalDateTime.now())
                .build();

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .build();

        assertThat(booking1.compareTo(booking2), equalTo(1));
    }
}
