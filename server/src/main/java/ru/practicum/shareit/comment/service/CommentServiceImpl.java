package ru.practicum.shareit.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.dto.CommentToGetDto;
import ru.practicum.shareit.comment.dto.CommentToReturnDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.JpaCommentRepository;
import ru.practicum.shareit.exception.CommentNotFoundException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UnavailableException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    final JpaCommentRepository commentRepository;
    final JpaUserRepository userRepository;
    final JpaItemRepository itemRepository;
    final JpaBookingRepository bookingRepository;
    final CommentMapper commentMapper;


    @Override
    public CommentToReturnDto getById(Long commentId) {
        return commentMapper.toReturnDto(checkCommentExistence(commentId));
    }

    @Override
    public CommentToReturnDto add(CommentToGetDto comment, Long userId, Long itemId) {
        User author = checkUserExistence(userId);
        Item item = checkItemExistence(itemId);
        if (item.getOwner().getId().equals(author.getId())) {
            throw new UnavailableException("Нельзя прокомментировать вещь, которой Вы владеете");
        }
        if (bookingRepository.findAllSuccessfulBookings(userId, itemId).isEmpty()) {
            throw new UnavailableException("Нельзя прокомментировать вещь, которую не бронировали.");
        }
        return commentMapper.toReturnDto(
                commentRepository.save(commentMapper.toEntity(comment, author, item)));
    }

    private User checkUserExistence(Long userId) {
        try {
            User user = userRepository.getReferenceById(userId);
            System.out.println(user);
            return user;
        } catch (EntityNotFoundException e) {
            throw new UserNotFoundException(userId);
        }
    }

    private Item checkItemExistence(Long itemId) {
        try {
            Item item = itemRepository.getReferenceById(itemId);
            System.out.println(item);
            return item;

        } catch (EntityNotFoundException e) {
            throw new ItemNotFoundException(itemId);
        }
    }

    private Comment checkCommentExistence(Long commentId) {
        try {
            Comment comment = commentRepository.getReferenceById(commentId);
            System.out.println(comment);
            return comment;
        } catch (EntityNotFoundException e) {
            throw new CommentNotFoundException(commentId);
        }
    }

}
