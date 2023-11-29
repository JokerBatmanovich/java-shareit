package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.Create;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookingToGetDto {
    Long id;
    @NotNull(groups = {Create.class},  message = "ID предмета не должен быть пустым.")
    Long itemId;
    @NotNull(groups = {Create.class},  message = "Начало бронирования должно быть указано.")
    @Future (groups = {Create.class},  message = "Начало не может быть в прошлом.")
    LocalDateTime start;
    @NotNull(groups = {Create.class},  message = "Конец бронирования должно быть указано.")
    @Future (groups = {Create.class},  message = "Конец не может быть в прошлом.")
    LocalDateTime end;
}
