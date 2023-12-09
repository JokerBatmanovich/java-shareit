package ru.practicum.shareit.comment.dto;

import lombok.*;
import ru.practicum.shareit.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NotNull(groups = {Create.class})
public class CommentToGetDto {
    private Long id;
    @NotBlank(groups = {Create.class},  message = "Название вещи не должно быть пустым.")
    private String text;
}
