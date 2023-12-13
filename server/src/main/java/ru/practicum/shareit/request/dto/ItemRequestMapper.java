package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class ItemRequestMapper {

    public ItemRequest toEntity(ItemRequestToGetDto itemRequestToGetDto, User user) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestToGetDto.getId());
        itemRequest.setDescription(itemRequestToGetDto.getDescription());
        itemRequest.setRequester(user);
        return itemRequest;
    }

    public ItemRequestToReturnDto toReturnDto(ItemRequest request, List<ItemForRequestDto> items) {
        ItemRequestToReturnDto requestToReturn = new ItemRequestToReturnDto();
        requestToReturn.setId(request.getId());
        requestToReturn.setDescription(request.getDescription());
        requestToReturn.setCreated(request.getCreated());
        requestToReturn.setItems(items);
        return requestToReturn;
    }

    public List<ItemRequestToReturnDto> toReturnDtoList(List<ItemRequest> requests, List<ItemForRequestDto> items) {
        Map<Long, List<ItemForRequestDto>> itemsMap = new HashMap<>();
        List<ItemRequestToReturnDto> toReturnDtoList = new ArrayList<>();
        for (ItemRequest request : requests) {
            itemsMap.put(request.getId(), new ArrayList<>());
        }
        for (ItemForRequestDto item : items) {
            itemsMap.get(item.getRequestId()).add(item);
        }
        for (ItemRequest request : requests) {
            toReturnDtoList.add(toReturnDto(request, itemsMap.get(request.getId())));
        }
        return toReturnDtoList;
    }

}
