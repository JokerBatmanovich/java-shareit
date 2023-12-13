package ru.practicum.shareit.comment.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yml")
class CommentRepositoryTest {

    @Autowired private JpaItemRepository itemRepository;
    @Autowired private JpaUserRepository userRepository;
    @Autowired private JpaCommentRepository commentRepository;

    @Test
    @Transactional
    void findAllByItemId() {
        User user1 = User.builder().name("name1").email("email1@mail.ru").build();
        userRepository.save(user1);
        User user2 = User.builder().name("name2").email("email2@mail.ru").build();
        userRepository.save(user2);
        User user3 = User.builder().name("name3").email("email3@mail.ru").build();
        userRepository.save(user3);

        Item item1 = Item.builder()
                .available(true).owner(user1).description("descr1").name("name1").build();
        itemRepository.save(item1);
        Item item2 = Item.builder()
                .available(true).owner(user3).description("descr2").name("name2").build();
        itemRepository.save(item2);

        Comment comment1 = Comment.builder()
                .created(LocalDateTime.now()).author(user1).text("Comment1 text").item(item2).build();
        commentRepository.save(comment1);
        Comment comment2 = Comment.builder()
                .created(LocalDateTime.now()).author(user2).text("Comment1 text").item(item1).build();
        commentRepository.save(comment2);
        Comment comment3 = Comment.builder()
                .created(LocalDateTime.now()).author(user3).text("Comment1 text").item(item1).build();
        commentRepository.save(comment3);

        List<Comment> actualComments = commentRepository.findAllByItemId(item1.getId());
        assertEquals(actualComments.size(), 2);
        assertTrue(actualComments.contains(comment2));
        assertTrue(actualComments.contains(comment3));
    }

    @Test
    @Transactional
    void findAllByItemId_shouldReturnEmptyList() {
        User user1 = User.builder().name("name1").email("email1@mail.ru").build();
        userRepository.save(user1);
        User user2 = User.builder().name("name2").email("email2@mail.ru").build();
        userRepository.save(user2);
        User user3 = User.builder().name("name3").email("email3@mail.ru").build();
        userRepository.save(user3);

        Item item1 = Item.builder()
                .available(true).owner(user1).description("descr1").name("name1").build();
        itemRepository.save(item1);
        Item item2 = Item.builder()
                .available(true).owner(user3).description("descr2").name("name2").build();
        itemRepository.save(item2);

        Comment comment1 = Comment.builder()
                .created(LocalDateTime.now()).author(user1).text("Comment1 text").item(item2).build();
        commentRepository.save(comment1);
        Comment comment2 = Comment.builder()
                .created(LocalDateTime.now()).author(user2).text("Comment1 text").item(item1).build();
        commentRepository.save(comment2);
        Comment comment3 = Comment.builder()
                .created(LocalDateTime.now()).author(user3).text("Comment1 text").item(item1).build();
        commentRepository.save(comment3);

        List<Comment> actualComments = commentRepository.findAllByItemId(item2.getId() + 1);
        assertEquals(actualComments.size(), 0);
    }
}
