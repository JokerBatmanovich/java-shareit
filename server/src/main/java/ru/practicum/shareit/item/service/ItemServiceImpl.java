package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.JpaCommentRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.NoPermissionException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemToGetDto;
import ru.practicum.shareit.item.dto.ItemToReturnDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    final JpaItemRepository itemRepository;
    final JpaUserRepository userRepository;
    final JpaBookingRepository bookingRepository;
    final JpaCommentRepository commentRepository;
    final ItemMapper itemMapper;
    final BookingMapper bookingMapper;

    @Override
    public ItemToReturnDto getById(Long itemId, Long userId) {
        checkUserExistence(userId);
        Item item = checkItemExistence(itemId);

        List<BookingForItemDto> bookings = item.getOwner().getId().equals(userId)
                ? bookingMapper.toForItemDtoList(bookingRepository.findAllByItemId(itemId)
                .stream()
                .filter(booking -> !booking.getStatus().equals(Status.REJECTED))
                .filter(booking -> !booking.getStatus().equals(Status.CANCELED))
                .collect(Collectors.toList()))
                : new ArrayList<>();
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        return itemMapper.toReturnDto(itemRepository.getReferenceById(itemId),
                bookings,
                comments);
    }

    @Override
    public List<ItemToReturnDto> getByOwnerId(Long userId, Integer from, Integer size) {
        checkUserExistence(userId);
        Pageable page = PageRequest.of(from / size, size);
//        List<Item> items = sublist(itemRepository.findAllByOwnerIdOrderById(userId), from, size);
        return toItemToReturnDtoList(itemRepository.findAllByOwnerIdOrderById(userId, page));
    }

    @Override
    public ItemToReturnDto add(ItemToGetDto newItem, Long userId) {
        User user = checkUserExistence(userId);
        Item item = itemMapper.toEntity(newItem);
        item.setOwner(user);
        return itemMapper.toReturnDto(itemRepository.save(item), new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public ItemToReturnDto update(ItemToGetDto itemToGetDto, Long itemId, Long userId) {
        checkUserExistence(userId);
        itemToGetDto.setId(itemId);
        Item oldItem = checkItemExistence(itemToGetDto.getId());
        checkUserPermissions(oldItem, userId);

        if (itemToGetDto.getName() != null) {
            oldItem.setName(itemToGetDto.getName());
        }
        if (itemToGetDto.getDescription() != null) {
            oldItem.setDescription(itemToGetDto.getDescription());
        }
        if (itemToGetDto.getAvailable() != null) {
            oldItem.setAvailable(itemToGetDto.getAvailable());
        }

        List<BookingForItemDto> bookings = bookingMapper.toForItemDtoList(
                bookingRepository.findAllByItemId(itemToGetDto.getId()));
        List<Comment> comments = commentRepository.findAllByItemId(itemToGetDto.getId());
        return itemMapper.toReturnDto(itemRepository.save(oldItem), bookings, comments);
    }

    @Override
    public List<ItemToReturnDto> search(String text, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        List<Item> items = itemRepository.search(text, page);
        return toItemToReturnDtoList(items);
    }

    private void checkUserPermissions(Item item, Long userId) {
        if (!item.getOwner().getId().equals(userId)) {
            throw new NoPermissionException(userId);
        }
    }

    private User checkUserExistence(Long userId) {
        try {
            User user = userRepository.getReferenceById(userId);
            System.out.println(user);
            return user;

        } catch (EntityNotFoundException e) {
            throw new UserNotFoundException(userId);
        }

    }

    private Item checkItemExistence(Long itemId) {
        try {
            Item item = itemRepository.getReferenceById(itemId);
            System.out.println(item);
            return item;
        } catch (EntityNotFoundException e) {
            throw new ItemNotFoundException(itemId);
        }
    }

    private List<ItemToReturnDto> toItemToReturnDtoList(List<Item> items) {
        List<ItemToReturnDto> itemToReturnDtoList = new ArrayList<>();
        List<Booking> bookings;
        List<Comment> comments;

        for (Item item : items) {
            bookings = bookingRepository.findAllByItemId(item.getId());
            comments = commentRepository.findAllByItemId(item.getId());
            itemToReturnDtoList.add(itemMapper.toReturnDto(item,
                    bookingMapper.toForItemDtoList(bookings),
                    comments));
        }
        return itemToReturnDtoList;
    }
}
