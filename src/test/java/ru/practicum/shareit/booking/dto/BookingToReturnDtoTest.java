package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class BookingToReturnDtoTest {
    @Test
    void compareToTest() {
        BookingToReturnDto booking1 = BookingToReturnDto.builder()
                .start(LocalDateTime.now())
                .build();

        BookingToReturnDto booking2 = BookingToReturnDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .build();

        assertThat(booking1.compareTo(booking2), equalTo(1));
    }
}
