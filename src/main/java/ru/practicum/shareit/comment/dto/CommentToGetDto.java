package ru.practicum.shareit.comment.dto;

import lombok.*;
import ru.practicum.shareit.Create;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommentToGetDto {
    private Long id;
    @NotBlank(groups = {Create.class},  message = "Название вещи не должно быть пустым.")
    String text;
}
