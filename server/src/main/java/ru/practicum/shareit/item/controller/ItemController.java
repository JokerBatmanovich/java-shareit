package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.comment.dto.CommentToGetDto;
import ru.practicum.shareit.comment.dto.CommentToReturnDto;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.exception.InvalidIdException;
import ru.practicum.shareit.item.dto.ItemToGetDto;
import ru.practicum.shareit.item.dto.ItemToReturnDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping(path = "/items")
@Validated
@RequiredArgsConstructor
public class ItemController {
    @Qualifier("itemServiceImpl")
    final ItemService itemService;
    @Qualifier("commentServiceImpl")
    final CommentService commentService;


    @GetMapping
    public List<ItemToReturnDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestParam(name = "from", defaultValue = "0") Integer from,
                                        @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemService.getByOwnerId(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemToReturnDto get(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        return itemService.getById(itemId, userId);
    }

    @PostMapping()
    public ItemToReturnDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @Validated({Create.class}) @RequestBody ItemToGetDto itemToGetDto) {

        return itemService.add(itemToGetDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemToReturnDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @Validated({Update.class}) @RequestBody ItemToGetDto itemToGetDto,
                                  @PathVariable Long itemId) {
        if (itemToGetDto.getId() != null && !itemToGetDto.getId().equals(itemId)) {
            throw new InvalidIdException("ID тела запроса не совпадает с ID из параметров.");
        }
        return itemService.update(itemToGetDto, itemId, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentToReturnDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @Validated({Create.class}) @RequestBody CommentToGetDto commentToGetDto,
                                       @PathVariable Long itemId) {
        return commentService.add(commentToGetDto, userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemToReturnDto> search(@RequestParam(name = "text", defaultValue = "") String text,
                                        @RequestParam(name = "from", defaultValue = "0") Integer from,
                                        @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemService.search(text, from, size);
    }
}
