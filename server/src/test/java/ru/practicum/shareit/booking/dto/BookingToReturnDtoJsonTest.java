package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemToReturnDto;
import ru.practicum.shareit.user.dto.UserToReturnDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingToReturnDtoJsonTest {
    @Autowired
    private JacksonTester<BookingToReturnDto> json;

    @Test
    void testBookingToReturnDto() throws Exception {
        BookingToReturnDto bookingToReturnDto = BookingToReturnDto.builder()
                .id(1L)
                .status(Status.APPROVED)
                .item(ItemToReturnDto.builder().id(1L).name("item").description("item description").build())
                .booker(UserToReturnDto.builder().id(1L).name("user").email("email@mail.ru").build())
                .end(LocalDateTime.of(2023, 12, 10, 10, 0, 0))
                .start(LocalDateTime.of(2023, 12, 8, 10, 0, 0))
                .build();
        JsonContent<BookingToReturnDto> result = json.write(bookingToReturnDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("item");
        assertThat(result).extractingJsonPathStringValue("$.item.description")
                                                                            .isEqualTo("item description");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("user");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("email@mail.ru");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-12-10T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-12-08T10:00:00");
    }
}
