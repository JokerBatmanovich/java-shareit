package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NotNull(groups = {Create.class, Update.class})
public class ItemToGetDto {
    private Long id;
    @NotBlank(groups = {Create.class},  message = "Название вещи не должно быть пустым.")
    private String name;
    @NotBlank(groups = {Create.class}, message = "Описание вещи не должно быть пустым.")
    private String description;
    @NotNull(groups = {Create.class},  message = "Статус доступности должен быть указан.")
    private Boolean available;
    private Long requestId;
}
