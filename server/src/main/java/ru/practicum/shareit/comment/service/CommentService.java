package ru.practicum.shareit.comment.service;

import ru.practicum.shareit.comment.dto.CommentToGetDto;
import ru.practicum.shareit.comment.dto.CommentToReturnDto;

public interface CommentService {

    CommentToReturnDto getById(Long commentId);

    CommentToReturnDto add(CommentToGetDto comment, Long userId, Long itemId);
}
