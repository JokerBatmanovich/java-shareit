package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    String description;
    User requester;
    LocalDateTime created;
}
