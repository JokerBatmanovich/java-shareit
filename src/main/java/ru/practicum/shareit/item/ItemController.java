package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.InvalidIdException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.exception.NoPermissionException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.Update;
import java.util.List;

@RestController
@RequestMapping(path = "/items")
@Validated
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    @Qualifier("itemServiceImpl")
    final ItemService itemService;
    final ItemMapper itemMapper;

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemMapper.toItemDtoList(itemService.getByUserId(userId));
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable Long itemId) {
        return itemMapper.toItemDto(itemService.get(itemId));
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        checkUserPermissions(userId, itemId);
        itemService.deleteById(itemId);
    }

    @PostMapping()
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                    @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        return itemMapper.toItemDto(itemService.add(itemMapper.toItem(itemDto), userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @Validated({Update.class}) @RequestBody ItemDto itemDto, @PathVariable Long itemId) {
        checkUserPermissions(userId, itemId);
        if (itemDto.getId() == null || itemDto.getId().equals(itemId)) {
            itemDto.setId(itemId);
        } else {
            throw new InvalidIdException("ID тела запроса не совпадает с ID из параметров.");
        }
        return itemMapper.toItemDto(itemService.update(itemMapper.toItem(itemDto)));
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(name = "text") String text) {
        return itemMapper.toItemDtoList(itemService.search(text));
    }

    private void checkUserPermissions(Long userId, Long itemId) {
        if (!userId.equals(itemService.get(itemId).getOwner().getId())) {
            throw new NoPermissionException(userId);
        }
    }
}
