package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemToReturnDto;
import ru.practicum.shareit.user.dto.UserToReturnDto;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingToReturnDto implements Comparable<BookingToReturnDto> {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
    private ItemToReturnDto item;
    private UserToReturnDto booker;

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
