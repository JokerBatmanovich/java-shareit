package ru.practicum.shareit.comment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.dto.CommentToReturnDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.JpaCommentRepository;
import ru.practicum.shareit.exception.CommentNotFoundException;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.user.dto.UserToReturnDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
                classes = {CommentService.class, CommentMapper.class})
@ContextConfiguration(classes = {CommentServiceImpl.class, CommentMapper.class})
public class CommentServiceTest {
    @SpyBean private CommentMapper commentMapper;
    @Autowired private CommentService commentService;
    @MockBean private JpaCommentRepository commentRepository;
    @MockBean private JpaUserRepository userRepository;
    @MockBean private JpaItemRepository itemRepository;
    @MockBean private JpaBookingRepository bookingRepository;

    User user1;
    UserToReturnDto user1ReturnDto;

    Comment comment1;
    CommentToReturnDto comment1ReturnDto;

    @BeforeEach
    void setUp() {
        user1 = User.builder().id(1L).name("name1").email("email1@mail.ru").build();
        user1ReturnDto = UserToReturnDto.builder().id(1L).name("name1").email("email1@mail.ru").build();

        comment1 = Comment.builder().id(1L).created(LocalDateTime.now()).author(user1).text("text1").build();
        comment1ReturnDto = CommentToReturnDto.builder()
                .id(1L)
                .created(comment1.getCreated())
                .authorName(user1.getName())
                .text("text1")
                .build();
    }

    @Test
    void getById() {
        Mockito.when(commentRepository.getReferenceById(comment1.getId()))
                .thenReturn(comment1);

        CommentToReturnDto actualComment = commentService.getById(comment1.getId());
        assertThat(actualComment, equalTo(comment1ReturnDto));

        Mockito.verify(commentRepository, Mockito.times(1))
                .getReferenceById(comment1.getId());
        Mockito.verifyNoMoreInteractions(commentRepository);
        Mockito.verify(commentMapper, Mockito.times(1))
                .toReturnDto(comment1);
        Mockito.verifyNoMoreInteractions(commentMapper);
    }

    @Test
    void getById_ShouldThrowCommentNotFoundException() {
        Mockito.when(commentRepository.getReferenceById(comment1.getId() + 1))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(CommentNotFoundException.class, () -> commentService.getById(comment1.getId() + 1));

        Mockito.verify(commentRepository, Mockito.times(1))
                .getReferenceById(comment1.getId() + 1);
        Mockito.verifyNoMoreInteractions(commentRepository);
        Mockito.verifyNoInteractions(commentMapper);
    }
}