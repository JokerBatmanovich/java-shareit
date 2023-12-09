package ru.practicum.shareit.comment.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommentToReturnDto {
    Long id;
    String text;
    String authorName;
    LocalDateTime created;

}
