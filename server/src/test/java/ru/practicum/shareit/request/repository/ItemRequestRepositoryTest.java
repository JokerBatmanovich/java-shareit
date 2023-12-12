package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yml")
class ItemRequestRepositoryTest {

    @Autowired private JpaUserRepository userRepository;
    @Autowired private JpaItemRequestRepository requestRepository;

    @Test
    @Transactional
    void findAllByRequesterIdIsNot() {
        User user1 = User.builder().name("name1").email("email1@mail.ru").build();
        userRepository.save(user1);
        User user2 = User.builder().name("name2").email("email2@mail.ru").build();
        userRepository.save(user2);

        ItemRequest request1 = ItemRequest.builder()
                .created(LocalDateTime.now()).requester(user2).description("request1 description").build();
        requestRepository.save(request1);
        ItemRequest request2 = ItemRequest.builder()
                .created(LocalDateTime.now()).requester(user1).description("request2 description").build();
        requestRepository.save(request2);
        ItemRequest request3 = ItemRequest.builder()
                .created(LocalDateTime.now()).requester(user2).description("request3 description").build();
        requestRepository.save(request3);
        ItemRequest request4 = ItemRequest.builder()
                .created(LocalDateTime.now()).requester(user2).description("request3 description").build();
        requestRepository.save(request4);
        ItemRequest request5 = ItemRequest.builder()
                .created(LocalDateTime.now()).requester(user1).description("request3 description").build();
        requestRepository.save(request5);

        List<ItemRequest> actualRequests =
                requestRepository.findAllByRequesterIdIsNotOrderByCreatedDesc(user2.getId(), PageRequest.of(0, 10));
        assertNotNull(actualRequests);
        assertEquals(actualRequests.size(), 2);
        assertTrue(actualRequests.contains(request2));
        assertTrue(actualRequests.contains(request5));
    }

    @Test
    @Transactional
    void findAllByRequesterIdIsNot_shouldReturnEmptyList() {
        User user1 = User.builder().name("name1").email("email1@mail.ru").build();
        userRepository.save(user1);
        User user2 = User.builder().name("name2").email("email2@mail.ru").build();
        userRepository.save(user2);

        ItemRequest request1 = ItemRequest.builder()
                .created(LocalDateTime.now()).requester(user2).description("request1 description").build();
        requestRepository.save(request1);
        ItemRequest request2 = ItemRequest.builder()
                .created(LocalDateTime.now()).requester(user2).description("request3 description").build();
        requestRepository.save(request2);
        ItemRequest request3 = ItemRequest.builder()
                .created(LocalDateTime.now()).requester(user2).description("request3 description").build();
        requestRepository.save(request3);

        List<ItemRequest> actualRequests =
                requestRepository.findAllByRequesterIdIsNotOrderByCreatedDesc(user2.getId(), PageRequest.of(0, 10));
        assertNotNull(actualRequests);
        assertEquals(actualRequests.size(), 0);
    }

    @Test
    @Transactional
    void findAllByRequesterId() {
        User user1 = User.builder().name("name1").email("email1@mail.ru").build();
        userRepository.save(user1);
        User user2 = User.builder().name("name2").email("email2@mail.ru").build();
        userRepository.save(user2);

        ItemRequest request1 = ItemRequest.builder()
                .created(LocalDateTime.now()).requester(user2).description("request1 description").build();
        requestRepository.save(request1);
        ItemRequest request2 = ItemRequest.builder()
                .created(LocalDateTime.now()).requester(user1).description("request2 description").build();
        requestRepository.save(request2);
        ItemRequest request3 = ItemRequest.builder()
                .created(LocalDateTime.now()).requester(user2).description("request3 description").build();
        requestRepository.save(request3);
        ItemRequest request4 = ItemRequest.builder()
                .created(LocalDateTime.now()).requester(user2).description("request3 description").build();
        requestRepository.save(request4);
        ItemRequest request5 = ItemRequest.builder()
                .created(LocalDateTime.now()).requester(user1).description("request3 description").build();
        requestRepository.save(request5);

        List<ItemRequest> actualRequests = requestRepository.findAllByRequesterId(user2.getId());
        assertNotNull(actualRequests);
        assertEquals(actualRequests.size(), 3);
        assertTrue(actualRequests.contains(request1));
        assertTrue(actualRequests.contains(request3));
        assertTrue(actualRequests.contains(request4));
    }

    @Test
    @Transactional
    void findAllByRequesterId_shouldReturnEmptyList() {
        User user1 = User.builder().name("name1").email("email1@mail.ru").build();
        userRepository.save(user1);
        User user2 = User.builder().name("name2").email("email2@mail.ru").build();
        userRepository.save(user2);

        ItemRequest request1 = ItemRequest.builder()
                .created(LocalDateTime.now()).requester(user2).description("request1 description").build();
        requestRepository.save(request1);
        ItemRequest request2 = ItemRequest.builder()
                .created(LocalDateTime.now()).requester(user1).description("request2 description").build();
        requestRepository.save(request2);
        ItemRequest request3 = ItemRequest.builder()
                .created(LocalDateTime.now()).requester(user2).description("request3 description").build();
        requestRepository.save(request3);
        ItemRequest request4 = ItemRequest.builder()
                .created(LocalDateTime.now()).requester(user2).description("request3 description").build();
        requestRepository.save(request4);
        ItemRequest request5 = ItemRequest.builder()
                .created(LocalDateTime.now()).requester(user1).description("request3 description").build();
        requestRepository.save(request5);

        List<ItemRequest> actualRequests = requestRepository.findAllByRequesterId(user2.getId() + 1);
        assertNotNull(actualRequests);
        assertEquals(actualRequests.size(), 0);
    }


}
