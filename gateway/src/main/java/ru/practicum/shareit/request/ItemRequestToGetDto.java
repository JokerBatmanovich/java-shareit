package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NotNull(groups = {Create.class, Update.class})
public class ItemRequestToGetDto {
    private Long id;
    @NotBlank(groups = {Create.class, Update.class},  message = "Описание запроса не должно быть пустым.")
    private String description;
    private Long requesterId;
}
