package ru.practicum.shareit.comment.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.comment.dto.CommentToGetDto;
import ru.practicum.shareit.comment.dto.CommentToReturnDto;
import ru.practicum.shareit.comment.repository.JpaCommentRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UnavailableException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentServiceIntegrationTest {
    @Autowired CommentService commentService;
    @Autowired JpaBookingRepository bookingRepository;
    @Autowired JpaUserRepository userRepository;
    @Autowired JpaItemRepository itemRepository;
    @Autowired JpaCommentRepository commentRepository;

    private User user1;
    private User user2;

    private Item item1;
    private Item item2;

    private CommentToGetDto commentGetDto;


    @BeforeEach
    void setup() {
        user1 = userRepository.save(User.builder().name("user1").email("email1@mail.ru").build());
        user2 = userRepository.save(User.builder().name("user2").email("email2@mail.ru").build());

        item1 = itemRepository.save(Item.builder()
                .owner(user1).name("item1").description("descr1").available(true).build());
        item2 = itemRepository.save(Item.builder()
                .owner(user2).name("item2").description("descr2").available(false).build());

        bookingRepository.save(Booking.builder()
                .booker(user1)
                .item(item2)
                .end(LocalDateTime.now().minusWeeks(1))
                .start(LocalDateTime.now().minusWeeks(2))
                .status(Status.APPROVED)
                .build());
        bookingRepository.save(Booking.builder()
                .booker(user2)
                .item(item1)
                .end(LocalDateTime.now().minusWeeks(1))
                .start(LocalDateTime.now().minusWeeks(2))
                .status(Status.CANCELED)
                .build());
        bookingRepository.save(Booking.builder()
                .booker(user1)
                .item(item2)
                .end(LocalDateTime.now().minusWeeks(1))
                .start(LocalDateTime.now().minusWeeks(2))
                .status(Status.CANCELED)
                .build());
       bookingRepository.save(Booking.builder()
                .booker(user2)
                .item(item1)
                .end(LocalDateTime.now().minusWeeks(1))
                .start(LocalDateTime.now().minusWeeks(2))
                .status(Status.REJECTED)
                .build());

        commentGetDto = CommentToGetDto.builder()
                .text("comment").build();
    }

    @AfterEach
    void clear() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        commentRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    void add() {
        CommentToReturnDto actualComment = commentService.add(commentGetDto, user1.getId(), item2.getId());
        assertNotNull(actualComment.getId());
        assertThat(actualComment.getAuthorName(), equalTo(user1.getName()));
        assertThat(actualComment.getText(), equalTo(commentGetDto.getText()));
        assertTrue(actualComment.getCreated().isBefore(LocalDateTime.now()));
    }

    @Test
    void add_shouldReturnUnavailableExceptionByOwner() {
        assertThrows(UnavailableException.class, () ->
                commentService.add(commentGetDto, user1.getId(), item1.getId()));
    }

    @Test
    void add_shouldReturnUnavailableExceptionByNoSuccessfulBookings() {
        assertThrows(UnavailableException.class, () ->
                commentService.add(commentGetDto, user2.getId(), item1.getId()));
    }

    @Test
    void add_shouldReturnItemNotFoundException() {
        assertThrows(ItemNotFoundException.class, () ->
                commentService.add(commentGetDto, user2.getId(), item2.getId() + 1));
    }

    @Test
    void add_shouldReturnUserNotFoundException() {
        assertThrows(UserNotFoundException.class, () ->
                commentService.add(commentGetDto, user2.getId() + 1, item2.getId()));
    }
}
