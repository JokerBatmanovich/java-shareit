package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestToGetDto;
import ru.practicum.shareit.request.dto.ItemRequestToReturnDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.JpaItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    final ItemRequestMapper requestMapper;
    final JpaItemRequestRepository requestRepository;
    final JpaItemRepository itemRepository;
    final JpaUserRepository userRepository;
    final ItemMapper itemMapper;

    @Override
    public ItemRequestToReturnDto getById(Long requestId, Long userId) {
        checkUserExistence(userId);
        ItemRequest request = checkRequestExistence(requestId);
        List<ItemForRequestDto> items = itemMapper.toForRequestDtoList(
                itemRepository.findAllByRequestId(requestId));
        return requestMapper.toReturnDto(request, items);
    }

    @Override
    public ItemRequestToReturnDto add(ItemRequestToGetDto newRequestDto, Long userId) {
        User user = checkUserExistence(userId);
        ItemRequest request = requestMapper.toEntity(newRequestDto, user);
        request.setCreated(LocalDateTime.now());
        return requestMapper.toReturnDto(requestRepository.save(request), new ArrayList<>());
    }

    @Override
    public List<ItemRequestToReturnDto> getByOwner(Long userId) {
        checkUserExistence(userId);
        List<ItemRequest> requests = requestRepository.findAllByRequesterId(userId);
        return toReturnDtoList(requests);
    }

    @Override
    public List<ItemRequestToReturnDto> getAll(Long userId, Integer from, Integer size) {
        checkUserExistence(userId);
        Pageable page = PageRequest.of(from / size, size);
        List<ItemRequest> requests = requestRepository.findAllByRequesterIdIsNotOrderByCreatedDesc(userId, page);
        return toReturnDtoList(requests);
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

    private ItemRequest checkRequestExistence(Long requestId) {
        try {
            ItemRequest request = requestRepository.getReferenceById(requestId);
            System.out.println(request);
            return request;
        } catch (EntityNotFoundException e) {
            throw new ItemRequestNotFoundException(requestId);
        }
    }

    private List<ItemRequestToReturnDto> toReturnDtoList(List<ItemRequest> requests) {
        Set<Long> requestsId = requests.stream().map(ItemRequest::getId).collect(Collectors.toSet());
        List<ItemForRequestDto> items;
        if (requestsId.size() != 0) {
            items = itemMapper.toForRequestDtoList(
                    itemRepository.findAllByRequestIdIn(requestsId));
        } else {
            items = new ArrayList<>();
        }
        return requestMapper.toReturnDtoList(requests, items);
    }
}
