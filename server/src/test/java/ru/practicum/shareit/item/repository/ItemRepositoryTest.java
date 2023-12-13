package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.JpaItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yml")
public class ItemRepositoryTest {

    @Autowired private JpaUserRepository userRepository;
    @Autowired private JpaItemRepository itemRepository;
    @Autowired private JpaItemRequestRepository requestRepository;

    @Test
    @Transactional
    void findAllByOwnerIdOrderById() {
        User user1 = User.builder().name("name1").email("email1@mail.ru").build();
        userRepository.save(user1);
        User user2 = User.builder().name("name2").email("email2@mail.ru").build();
        userRepository.save(user2);

        Item item1 = Item.builder() // подходит
                .available(true).owner(user1).description("descr1").name("name1").build();
        itemRepository.save(item1);
        Item item2 = Item.builder() // юзер2
                .available(true).owner(user2).description("descr2").name("name2").build();
        itemRepository.save(item2); // подходит
        Item item3 = Item.builder()
                .available(true).owner(user1).description("descr3").name("name3").build();
        itemRepository.save(item3);
        Item item4 = Item.builder() // юзер2
                .available(true).owner(user2).description("descr4").name("name4").build();
        itemRepository.save(item4);

        List<Item> actualItems = itemRepository.findAllByOwnerIdOrderById(user1.getId(), PageRequest.of(0, 10));
        assertNotNull(actualItems);
        assertEquals(actualItems.size(), 2);
        assertTrue(actualItems.contains(item1));
        assertTrue(actualItems.contains(item3));
    }

    @Test
    @Transactional
    void findAllByOwnerIdOrderById_shouldReturnEmptyList() {
        User user1 = User.builder().name("name1").email("email1@mail.ru").build();
        userRepository.save(user1);
        User user2 = User.builder().name("name2").email("email2@mail.ru").build();
        userRepository.save(user2);

        Item item1 = Item.builder()
                .available(true).owner(user1).description("descr1").name("name1").build();
        itemRepository.save(item1);
        Item item2 = Item.builder()
                .available(true).owner(user2).description("descr2").name("name2").build();
        itemRepository.save(item2);
        Item item3 = Item.builder()
                .available(true).owner(user1).description("descr3").name("name3").build();
        itemRepository.save(item3);
        Item item4 = Item.builder()
                .available(true).owner(user2).description("descr4").name("name4").build();
        itemRepository.save(item4);

        List<Item> actualItems = itemRepository
                .findAllByOwnerIdOrderById(user1.getId() + 10, PageRequest.of(0, 10));
        assertNotNull(actualItems);
        assertEquals(actualItems.size(), 0);
    }

    @Test
    @Transactional
    void findAllByRequestIdIn() {
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
                .created(LocalDateTime.now()).requester(user1).description("request3 description").build();
        requestRepository.save(request3);

        Item item1 = Item.builder()
                .available(true).owner(user1).description("descr1").name("name1").requestId(request1.getId()).build();
        itemRepository.save(item1);
        Item item2 = Item.builder()
                .available(true).owner(user2).description("descr2").name("name2").requestId(request2.getId()).build();
        itemRepository.save(item2);
        Item item3 = Item.builder()
                .available(true).owner(user1).description("descr3").name("name3").build();
        itemRepository.save(item3);
        Item item4 = Item.builder()
                .available(true).owner(user2).description("descr4").name("name4").requestId(request3.getId()).build();
        itemRepository.save(item4);
        Set<Long> requestsIdsSet = Set.of(request1.getId(), request3.getId());

        List<Item> actualItems = itemRepository.findAllByRequestIdIn(requestsIdsSet);
        assertNotNull(actualItems);
        assertEquals(actualItems.size(), 2);
        assertTrue(actualItems.contains(item1));
        assertTrue(actualItems.contains(item4));
    }

    @Test
    @Transactional
    void findAllByRequestIdIn_shouldReturnEmptyList() {
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
                .created(LocalDateTime.now()).requester(user1).description("request3 description").build();
        requestRepository.save(request3);

        Item item1 = Item.builder()
                .available(true).owner(user1).description("descr1").name("name1").requestId(request1.getId()).build();
        itemRepository.save(item1);
        Item item2 = Item.builder()
                .available(true).owner(user2).description("descr2").name("name2").requestId(request2.getId()).build();
        itemRepository.save(item2);
        Item item3 = Item.builder()
                .available(true).owner(user1).description("descr3").name("name3").build();
        itemRepository.save(item3);
        Item item4 = Item.builder()
                .available(true).owner(user2).description("descr4").name("name4").requestId(request3.getId()).build();
        itemRepository.save(item4);
        Set<Long> requestsIdsSet = Set.of(request3.getId() + 1, request3.getId() + 2, request3.getId() + 3);

        List<Item> actualItems = itemRepository.findAllByRequestIdIn(requestsIdsSet);
        assertNotNull(actualItems);
        assertEquals(actualItems.size(), 0);
    }

    @Test
    @Transactional
    void findAllByRequestId() {
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

        Item item1 = Item.builder()
                .available(true).owner(user1).description("descr1").name("name1").requestId(request1.getId()).build();
        itemRepository.save(item1);
        Item item2 = Item.builder()
                .available(true).owner(user2).description("descr2").name("name2").build();
        itemRepository.save(item2);
        Item item3 = Item.builder()
                .available(true).owner(user1).description("descr3").name("name3").requestId(request1.getId()).build();
        itemRepository.save(item3);
        Item item4 = Item.builder()
                .available(true).owner(user2).description("descr4").name("name4").requestId(request2.getId()).build();
        itemRepository.save(item4);

        List<Item> actualItems = itemRepository.findAllByRequestId(request1.getId());
        assertNotNull(actualItems);
        assertEquals(actualItems.size(), 2);
        assertTrue(actualItems.contains(item1));
        assertTrue(actualItems.contains(item3));
    }

    @Test
    @Transactional
    void findAllByRequestId_shouldReturnEmptyList() {
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

        Item item1 = Item.builder()
                .available(true).owner(user1).description("descr1").name("name1").requestId(request1.getId()).build();
        itemRepository.save(item1);
        Item item2 = Item.builder()
                .available(true).owner(user2).description("descr2").name("name2").build();
        itemRepository.save(item2);
        Item item3 = Item.builder()
                .available(true).owner(user1).description("descr3").name("name3").requestId(request1.getId()).build();
        itemRepository.save(item3);
        Item item4 = Item.builder()
                .available(true).owner(user2).description("descr4").name("name4").requestId(request2.getId()).build();
        itemRepository.save(item4);

        List<Item> actualItems = itemRepository.findAllByRequestId(request2.getId() + 1);
        assertNotNull(actualItems);
        assertEquals(actualItems.size(), 0);
    }

    @Test
    @Transactional
    void search_shouldFindByEqualText() {
        User user1 = User.builder().name("name1").email("email1@mail.ru").build();
        userRepository.save(user1);
        User user2 = User.builder().name("name2").email("email2@mail.ru").build();
        userRepository.save(user2);

        Item item1 = Item.builder() // подходит
                .available(true).owner(user1).description("descr for SearCH").name("name1").build();
        itemRepository.save(item1);
        Item item2 = Item.builder() // недоступен
                .available(false).owner(user2).description("descr not for searhc").name("name forseARch").build();
        itemRepository.save(item2);
        Item item3 = Item.builder() // подходит
                .available(true).owner(user1).description("descrforSEARCH").name("name3").build();
        itemRepository.save(item3);
        Item item4 = Item.builder() // нет текста
                .available(true).owner(user2).description("descr4").name("name4").build();
        itemRepository.save(item4);
        Item item5 = Item.builder() // недоступен
                .available(false).owner(user1).description("descrforSEARCH").name("name5").build();
        itemRepository.save(item5);
        Item item6 = Item.builder() // подходит
                .available(true).owner(user2).description("SEARCH").name("name5").build();
        itemRepository.save(item6);

        List<Item> actualItems = itemRepository.search("SeArCh", PageRequest.of(0, 10));
        assertNotNull(actualItems);
        assertEquals(actualItems.size(), 3);
        assertTrue(actualItems.contains(item1));
        assertTrue(actualItems.contains(item3));
        assertTrue(actualItems.contains(item6));
    }

    @Test
    @Transactional
    void search_shouldReturnEmptyList() {
        User user1 = User.builder().name("name1").email("email1@mail.ru").build();
        userRepository.save(user1);
        User user2 = User.builder().name("name2").email("email2@mail.ru").build();
        userRepository.save(user2);

        Item item1 = Item.builder()
                .available(false).owner(user1).description("descr1").name("textName").build();
        itemRepository.save(item1);
        Item item2 = Item.builder()
                .available(true).owner(user2).description("descr2").name("name2").build();
        itemRepository.save(item2);
        Item item3 = Item.builder()
                .available(false).owner(user1).description("text").name("name3").build();
        itemRepository.save(item3);
        Item item4 = Item.builder()
                .available(true).owner(user2).description("descr4").name("name4").build();
        itemRepository.save(item4);

        List<Item> actualItems = itemRepository.search("text", PageRequest.of(0, 10));
        assertNotNull(actualItems);
        assertEquals(actualItems.size(), 0);
    }
}
