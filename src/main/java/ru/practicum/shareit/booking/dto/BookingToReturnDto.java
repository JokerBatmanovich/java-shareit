package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemToReturnDto;
import ru.practicum.shareit.user.dto.UserToReturnDto;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookingToReturnDto implements Comparable<BookingToReturnDto> {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    Status status;
    ItemToReturnDto item;
    UserToReturnDto booker;

    @Override
    public int compareTo(BookingToReturnDto o) {
        if (o.getStart().isAfter(this.getStart())) {
            return 1;
        } else if (this.getStart().isAfter(o.getStart())) {
            return -1;
        } else {
            return 0;
        }
    }
}
