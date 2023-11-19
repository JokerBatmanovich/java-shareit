package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    Long id;
    String description;
    User requestor;
    LocalDateTime created;
}
