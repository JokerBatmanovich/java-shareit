package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemForRequestDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestToReturnDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemForRequestDto> items;
}
