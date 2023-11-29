package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ItemToGetDto {
    private Long id;
    @NotBlank(groups = {Create.class},  message = "Название вещи не должно быть пустым.")
    String name;
    @NotBlank(groups = {Create.class}, message = "Описание вещи не должно быть пустым.")
    String description;
    @NotNull(groups = {Create.class},  message = "Статус доступности должен быть указан.")
    Boolean available;
}
