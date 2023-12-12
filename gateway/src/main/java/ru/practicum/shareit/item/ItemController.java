package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.exception.InvalidIdException;

import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/items")
@Validated
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;


    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
                                         @RequestParam(name = "size", defaultValue = "10") @Min(1) Integer size) {
        return itemClient.getUserItems(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        return itemClient.getItem(userId, itemId);
    }

    @PostMapping()
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @Validated({Create.class}) @RequestBody ItemToGetDto itemToGetDto) {

        return itemClient.add(userId, itemToGetDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Validated({Update.class}) @RequestBody ItemToGetDto itemToGetDto,
                                         @PathVariable Long itemId) {
        if (itemToGetDto.getId() != null && !itemToGetDto.getId().equals(itemId)) {
            throw new InvalidIdException("ID тела запроса не совпадает с ID из параметров.");
        }
        return itemClient.update(userId, itemId, itemToGetDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Validated({Create.class}) @RequestBody CommentToGetDto commentToGetDto,
                                             @PathVariable Long itemId) {
        return itemClient.addComment(userId, itemId, commentToGetDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam(name = "text", defaultValue = "") String text,
                                         @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
                                         @RequestParam(name = "size", defaultValue = "10") @Min(1) Integer size) {
        if (!text.isBlank()) {
            return itemClient.search(text, from, size);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

}
