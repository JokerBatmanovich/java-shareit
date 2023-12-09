package ru.practicum.shareit.user.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserToReturnDto {
    private Long id;
    private String name;
    private String email;
}
