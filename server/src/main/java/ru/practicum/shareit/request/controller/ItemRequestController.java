package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.request.dto.ItemRequestToGetDto;
import ru.practicum.shareit.request.dto.ItemRequestToReturnDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Validated
@RequiredArgsConstructor
public class ItemRequestController {

    @Qualifier("itemRequestServiceImpl")
    final ItemRequestService requestService;


    @PostMapping()
    public ItemRequestToReturnDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestBody @Validated({Create.class}) ItemRequestToGetDto requestToGetDto) {
        return requestService.add(requestToGetDto, userId);
    }

    @GetMapping()
    public List<ItemRequestToReturnDto> get(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getByOwner(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestToReturnDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(name = "from", defaultValue = "0") Integer from,
                                               @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return requestService.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestToReturnDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable Long requestId) {
        return requestService.getById(requestId, userId);
    }

}
