package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemMapper {
    final CommentMapper commentMapper;

    public Item toEntity(ItemToGetDto itemToGetDto) {
        Item item = new Item();
        item.setId(itemToGetDto.getId());
        item.setName(itemToGetDto.getName());
        item.setDescription(itemToGetDto.getDescription());
        item.setAvailable(itemToGetDto.getAvailable());
        item.setRequestId(itemToGetDto.getRequestId());
        return item;
    }

    public Item toEntity(ItemToReturnDto itemToReturnDto, User user) {
        Item item = new Item();
        item.setId(itemToReturnDto.getId());
        item.setName(itemToReturnDto.getName());
        item.setDescription(itemToReturnDto.getDescription());
        item.setAvailable(itemToReturnDto.getAvailable());
        item.setRequestId(itemToReturnDto.getRequestId());
        item.setOwner(user);
        return item;
    }

    public ItemToReturnDto toReturnDto(Item item,
                                       List<BookingForItemDto> bookings,
                                       List<Comment> comments) {
        ItemToReturnDto itemToReturnDto = new ItemToReturnDto();
        itemToReturnDto.setId(item.getId());
        itemToReturnDto.setName(item.getName());
        itemToReturnDto.setDescription(item.getDescription());
        itemToReturnDto.setAvailable(item.getAvailable());
        itemToReturnDto.setRequestId(item.getRequestId());
        itemToReturnDto.setComments(commentMapper.toReturnDtoList(comments));
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

    public ItemForRequestDto toForRequestDto(Item item) {
        ItemForRequestDto itemForRequestDto = new ItemForRequestDto();
        itemForRequestDto.setId(item.getId());
        itemForRequestDto.setName(item.getName());
        itemForRequestDto.setDescription(item.getDescription());
        itemForRequestDto.setAvailable(item.getAvailable());
        itemForRequestDto.setRequestId(item.getRequestId());
        return itemForRequestDto;
    }

    public List<ItemForRequestDto> toForRequestDtoList(List<Item> items) {
        List<ItemForRequestDto> listToReturn = new ArrayList<>();
        for (Item item : items) {
            listToReturn.add(toForRequestDto(item));
        }
        return listToReturn;
    }

}
