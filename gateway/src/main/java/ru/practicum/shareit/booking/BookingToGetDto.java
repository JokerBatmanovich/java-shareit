package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.Create;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingToGetDto {
    private Long id;
    @NotNull(groups = {Create.class},  message = "ID предмета не должен быть пустым.")
    private Long itemId;
    @NotNull(groups = {Create.class},  message = "Начало бронирования должно быть указано.")
    @Future (groups = {Create.class},  message = "Начало не может быть в прошлом.")
    private LocalDateTime start;
    @NotNull(groups = {Create.class},  message = "Конец бронирования должно быть указано.")
    @Future (groups = {Create.class},  message = "Конец не может быть в прошлом.")
    private LocalDateTime end;
}
