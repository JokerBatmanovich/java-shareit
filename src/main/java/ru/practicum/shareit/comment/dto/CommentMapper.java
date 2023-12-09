package ru.practicum.shareit.comment.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class CommentMapper {

    public Comment toComment(CommentToGetDto commentToGetDto, User author, Item item) {
        Comment comment = new Comment();
        comment.setText(commentToGetDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    public List<CommentToReturnDto> toCommentToReturnDtoList(List<Comment> commentsList) {
        List<CommentToReturnDto> listToReturn = new ArrayList<>();
        commentsList.forEach(comment -> listToReturn.add(toCommentToReturnDto(comment)));
        return listToReturn;
    }

    public CommentToReturnDto toCommentToReturnDto(Comment comment) {
        return new CommentToReturnDto(comment.getId(),
                                      comment.getText(),
                                      comment.getAuthor().getName(),
                                      comment.getCreated());
    }
}
