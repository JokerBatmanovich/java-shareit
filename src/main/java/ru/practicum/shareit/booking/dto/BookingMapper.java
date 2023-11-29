package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemToReturnDto;
import ru.practicum.shareit.user.dto.UserToReturnDto;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    final ItemMapper itemMapper;
    final CommentMapper commentMapper;

    public BookingToGetDto toBookingDto(Booking booking) {
        return new BookingToGetDto(booking.getId(),
                booking.getItem().getId(),
                booking.getStart(),
                booking.getEnd());
    }

    public BookingForItemDto toBookingForItemDto(Booking booking) {
        return new BookingForItemDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getBooker().getId()
        );
    }

    public List<BookingForItemDto> toBookingForItemDtoList(List<Booking> bookingList) {
        List<BookingForItemDto> listToReturn = new ArrayList<>();
        bookingList.forEach(booking -> listToReturn.add(toBookingForItemDto(booking)));
        return listToReturn;
    }

    public List<BookingToGetDto> toBookingDtoList(List<Booking> bookingList) {
        List<BookingToGetDto> listToReturn = new ArrayList<>();
        bookingList.forEach(booking -> listToReturn.add(toBookingDto(booking)));
        return listToReturn;
    }

    public Booking toBooking(BookingToGetDto bookingToGetDto) {
        Booking booking = new Booking();
        booking.setId(bookingToGetDto.getId());
        booking.setStart(bookingToGetDto.getStart());
        booking.setEnd(bookingToGetDto.getEnd());
        return booking;
    }

    public BookingToReturnDto toBookingReturnDto(Booking booking,
                                                 List<Booking> itemBookings,
                                                 List<Comment> itemComments) {
        ItemToReturnDto itemToReturnDto = itemMapper.toItemToReturnDto(booking.getItem(),
                                                                       toBookingForItemDtoList(itemBookings),
                                                                       itemComments);
        itemToReturnDto.setId(booking.getItem().getId());
        itemToReturnDto.setName(booking.getItem().getName());
        itemToReturnDto.setDescription(booking.getItem().getDescription());
        itemToReturnDto.setAvailable(booking.getItem().getAvailable());

        UserToReturnDto userDto = new UserToReturnDto();
        userDto.setId(booking.getBooker().getId());
        userDto.setName(booking.getBooker().getName());
        userDto.setEmail(booking.getBooker().getEmail());
        return new BookingToReturnDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                itemToReturnDto,
                userDto);
    }
}
