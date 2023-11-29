package ru.practicum.shareit.user.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserToReturnDto {
    private Long id;
    private String name;
    private String email;
}
