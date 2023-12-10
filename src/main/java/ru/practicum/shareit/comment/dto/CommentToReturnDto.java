package ru.practicum.shareit.comment.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentToReturnDto {
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;

}
