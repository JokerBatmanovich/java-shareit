package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookingForItemDto {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    Long bookerId;
}
