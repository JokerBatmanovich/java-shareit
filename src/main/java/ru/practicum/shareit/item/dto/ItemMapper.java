package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemMapper {
    final CommentMapper commentMapper;

    public Item toItem(ItemToGetDto itemToGetDto) {
        Item item = new Item();
        item.setId(itemToGetDto.getId());
        item.setName(itemToGetDto.getName());
        item.setDescription(itemToGetDto.getDescription());
        item.setAvailable(itemToGetDto.getAvailable());
        return item;
    }

    public ItemToReturnDto toItemToReturnDto(Item item,
                                             List<BookingForItemDto> bookings,
                                             List<Comment> comments) {
        ItemToReturnDto itemToReturnDto = new ItemToReturnDto();
        itemToReturnDto.setId(item.getId());
        itemToReturnDto.setName(item.getName());
        itemToReturnDto.setDescription(item.getDescription());
        itemToReturnDto.setAvailable(item.getAvailable());
        itemToReturnDto.setComments(commentMapper.toCommentToReturnDtoList(comments));
        LocalDateTime now = LocalDateTime.now();
        if (!bookings.isEmpty()) {
            for (BookingForItemDto booking : bookings) {
                if (booking.getStart().isAfter(now)) {
                    itemToReturnDto.setNextBooking(booking);
                }
                if (booking.getStart().isBefore(now)) {
                    itemToReturnDto.setLastBooking(booking);
                }
            }
            for (BookingForItemDto booking : bookings) {
                if (booking.getStart().isAfter(now)
                        && booking.getStart().isBefore(itemToReturnDto.getNextBooking().getStart())) {
                    itemToReturnDto.setNextBooking(booking);
                }
                if (booking.getStart().isBefore(now)
                        && (booking.getStart().isAfter(itemToReturnDto.getLastBooking().getStart()))) {
                    itemToReturnDto.setLastBooking(booking);
                }
            }
        }
        return itemToReturnDto;
    }

}
