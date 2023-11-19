package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    LocalDateTime start;
    LocalDateTime end;
    Item item;
}
