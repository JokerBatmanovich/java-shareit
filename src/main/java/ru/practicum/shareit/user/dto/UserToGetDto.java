package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserToGetDto {
    private Long id;
    @NotBlank(groups = {Create.class}, message = "Имя не может быть пустым.")
    private String name;
    @NotBlank(groups = {Create.class}, message = "Email не может быть пустым.")
    @Email(groups = {Create.class, Update.class}, message = "Некорректный email.")
    private String email;
}
