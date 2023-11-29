package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.comment.dto.CommentToReturnDto;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ItemToReturnDto {
    private Long id;
    String name;
    String description;
    Boolean available;
    BookingForItemDto lastBooking;
    BookingForItemDto nextBooking;
    List<CommentToReturnDto> comments;
}
