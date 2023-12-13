package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yml")
class BookingRepositoryTest {

    @Autowired private JpaBookingRepository bookingRepository;
    @Autowired private JpaItemRepository itemRepository;
    @Autowired private JpaUserRepository userRepository;

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

        Booking booking1 = Booking.builder()
                .item(item1).booker(user2).status(Status.WAITING)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .build();
        bookingRepository.save(booking1);
        Booking booking2 = Booking.builder()
                .item(item2).booker(user2).status(Status.WAITING)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .build();
        bookingRepository.save(booking2);
        Booking booking3 = Booking.builder()
                .item(item1).booker(user3).status(Status.WAITING)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .build();
        bookingRepository.save(booking3);

        List<Booking> actualBookings = bookingRepository.findAllByItemId(item1.getId());

        assertEquals(actualBookings.size(), 2);
        assertTrue(actualBookings.contains(booking1));
        assertTrue(actualBookings.contains(booking3));
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

        Booking booking1 = Booking.builder()
                .item(item1).booker(user2).status(Status.WAITING)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .build();
        bookingRepository.save(booking1);
        Booking booking2 = Booking.builder()
                .item(item2).booker(user2).status(Status.WAITING)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .build();
        bookingRepository.save(booking2);
        Booking booking3 = Booking.builder()
                .item(item1).booker(user3).status(Status.WAITING)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .build();
        bookingRepository.save(booking3);

        List<Booking> actualBookings = bookingRepository.findAllByItemId(item2.getId() + 10);

        assertNotNull(actualBookings);
        assertEquals(actualBookings.size(), 0);
    }

    @Test
    @Transactional
    void findAllSuccessfulBookings() {
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

        Booking booking1 = Booking.builder() //подходит
                .item(item1).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking1);
        Booking booking2 = Booking.builder() //отмененный
                .item(item1).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking2);
        Booking booking3 = Booking.builder() // юзер3
                .item(item1).booker(user3)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking3);
        Booking booking4 = Booking.builder() // итем2
                .item(item2).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking4);
        Booking booking5 = Booking.builder() // подходит
                .item(item1).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking5);
        Booking booking6 = Booking.builder() // время в будущем
                .item(item1).booker(user2)
                .start(LocalDateTime.now().minusMonths(1))
                .end(LocalDateTime.now().plusMonths(2))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking6);
        List<Booking> actualBookings = bookingRepository.findAllSuccessfulBookings(user2.getId(), item1.getId());
        assertNotNull(actualBookings);
        assertEquals(actualBookings.size(), 2);
        assertTrue(actualBookings.contains(booking1));
        assertTrue(actualBookings.contains(booking5));
    }

    @Test
    @Transactional
    void findAllSuccessfulBookings_shouldReturnEmptyList() {
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

        Booking booking1 = Booking.builder() //отмененный
                .item(item1).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking1);
        Booking booking2 = Booking.builder() // юзер3
                .item(item1).booker(user3)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking2);
        Booking booking3 = Booking.builder() // итем2
                .item(item2).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking3);
        Booking booking4 = Booking.builder() // время в будущем
                .item(item1).booker(user2)
                .start(LocalDateTime.now().plusMonths(1))
                .end(LocalDateTime.now().plusMonths(2))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking4);
        List<Booking> actualBookings = bookingRepository.findAllSuccessfulBookings(user2.getId(), item1.getId());
        assertNotNull(actualBookings);
        assertEquals(actualBookings.size(), 0);
    }

    @Test
    @Transactional
    void findAllUserItemsCurrentBookings() {
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

        Booking booking1 = Booking.builder() //подходит
                .item(item1).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking1);
        Booking booking2 = Booking.builder() //подходит
                .item(item1).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking2);
        Booking booking3 = Booking.builder() // прошедший
                .item(item1).booker(user3)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking3);
        Booking booking4 = Booking.builder() // итем2
                .item(item2).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking4);
        Booking booking5 = Booking.builder() // подходит
                .item(item1).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking5);
        Booking booking6 = Booking.builder() // старт в будущем
                .item(item1).booker(user2)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking6);
        List<Booking> actualBookings = bookingRepository
                .findAllUserItemsCurrentBookings(user1.getId(), PageRequest.of(0, 10));
        assertNotNull(actualBookings);
        assertEquals(actualBookings.size(), 3);
        assertTrue(actualBookings.contains(booking1));
        assertTrue(actualBookings.contains(booking2));
        assertTrue(actualBookings.contains(booking5));
    }

    @Test
    @Transactional
    void findAllUserItemsCurrentBookings_shouldReturnEmptyList() {
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

        Booking booking1 = Booking.builder() // прошедший
                .item(item1).booker(user3)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking1);
        Booking booking2 = Booking.builder() // итем2
                .item(item2).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking2);
        Booking booking3 = Booking.builder() // старт в будущем
                .item(item1).booker(user2)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking3);
        List<Booking> actualBookings = bookingRepository
                .findAllUserItemsCurrentBookings(user1.getId(), PageRequest.of(0, 10));
        assertNotNull(actualBookings);
        assertEquals(actualBookings.size(), 0);
    }

    @Test
    @Transactional
    void findAllUserItemsBookings() {
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
        Item item3 = Item.builder()
                .available(true).owner(user1).description("descr3").name("name3").build();
        itemRepository.save(item3);

        Booking booking1 = Booking.builder() //подходит
                .item(item1).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking1);
        Booking booking2 = Booking.builder() //подходит
                .item(item3).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking2);
        Booking booking3 = Booking.builder() // подходит
                .item(item1).booker(user3)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking3);
        Booking booking4 = Booking.builder() // итем2
                .item(item2).booker(user1)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking4);
        Booking booking5 = Booking.builder() // подходит
                .item(item1).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking5);
        Booking booking6 = Booking.builder() // итем2
                .item(item2).booker(user2)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking6);
        List<Booking> actualBookings = bookingRepository
                .findAllUserItemsBookings(user1.getId(), PageRequest.of(0, 10));
        assertNotNull(actualBookings);
        assertEquals(actualBookings.size(), 4);
        assertTrue(actualBookings.contains(booking1));
        assertTrue(actualBookings.contains(booking2));
        assertTrue(actualBookings.contains(booking3));
        assertTrue(actualBookings.contains(booking5));
    }

    @Test
    @Transactional
    void findAllUserItemsBookings_shouldReturnEmptyList() {
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
        Item item3 = Item.builder()
                .available(true).owner(user3).description("descr3").name("name3").build();
        itemRepository.save(item3);

        Booking booking1 = Booking.builder()
                .item(item2).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking1);
        Booking booking2 = Booking.builder()
                .item(item3).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking2);
        Booking booking3 = Booking.builder()
                .item(item2).booker(user1)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking3);
        Booking booking4 = Booking.builder()
                .item(item2).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking4);
        Booking booking5 = Booking.builder()
                .item(item3).booker(user2)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking5);
        List<Booking> actualBookings = bookingRepository
                .findAllUserItemsBookings(user1.getId(), PageRequest.of(0, 10));
        assertNotNull(actualBookings);
        assertEquals(actualBookings.size(), 0);
    }

    @Test
    @Transactional
    void findAllUserItemsPastBookings() {
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
        Item item3 = Item.builder()
                .available(true).owner(user1).description("descr3").name("name3").build();
        itemRepository.save(item3);

        Booking booking1 = Booking.builder() //действующая
                .item(item1).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking1);
        Booking booking2 = Booking.builder() //подходит
                .item(item3).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking2);
        Booking booking3 = Booking.builder() // отмененная
                .item(item1).booker(user3)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking3);
        Booking booking4 = Booking.builder() // итем2
                .item(item2).booker(user1)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking4);
        Booking booking5 = Booking.builder() // подходит
                .item(item1).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking5);
        Booking booking6 = Booking.builder() // в будущем
                .item(item2).booker(user2)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking6);
        List<Booking> actualBookings = bookingRepository
                .findAllUserItemsPastBookings(user1.getId(), PageRequest.of(0, 10));
        assertNotNull(actualBookings);
        assertEquals(actualBookings.size(), 2);
        assertTrue(actualBookings.contains(booking2));
        assertTrue(actualBookings.contains(booking5));
    }

    @Test
    @Transactional
    void findAllUserItemsPastBookings_shouldReturnEmptyList() {
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
        Item item3 = Item.builder()
                .available(true).owner(user1).description("descr3").name("name3").build();
        itemRepository.save(item3);

        Booking booking1 = Booking.builder() //действующая
                .item(item1).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking1);
        Booking booking2 = Booking.builder() //отмененная
                .item(item3).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.CANCELED)
                .build();
        bookingRepository.save(booking2);
        Booking booking3 = Booking.builder() // отказанная
                .item(item1).booker(user3)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking3);
        Booking booking4 = Booking.builder() // итем2
                .item(item2).booker(user1)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking4);
        Booking booking5 = Booking.builder() // в будущем
                .item(item2).booker(user2)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking5);
        List<Booking> actualBookings = bookingRepository
                .findAllUserItemsPastBookings(user1.getId(), PageRequest.of(0, 10));
        assertNotNull(actualBookings);
        assertEquals(actualBookings.size(), 0);
    }

    @Test
    @Transactional
    void findAllUserItemsFutureBookings() {
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
        Item item3 = Item.builder()
                .available(true).owner(user1).description("descr3").name("name3").build();
        itemRepository.save(item3);

        Booking booking1 = Booking.builder() //действующая
                .item(item1).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking1);
        Booking booking2 = Booking.builder() //подходит
                .item(item3).booker(user2)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking2);
        Booking booking3 = Booking.builder() // отмененная
                .item(item1).booker(user3)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking3);
        Booking booking4 = Booking.builder() // итем2
                .item(item2).booker(user1)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking4);
        Booking booking5 = Booking.builder() // подходит
                .item(item1).booker(user2)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking5);
        Booking booking6 = Booking.builder() // в прошлом
                .item(item2).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking6);
        List<Booking> actualBookings = bookingRepository
                .findAllUserItemsFutureBookings(user1.getId(), PageRequest.of(0, 10));
        assertNotNull(actualBookings);
        assertEquals(actualBookings.size(), 2);
        assertTrue(actualBookings.contains(booking2));
        assertTrue(actualBookings.contains(booking5));
    }

    @Test
    @Transactional
    void findAllUserItemsFutureBookings_shouldReturnEmptyList() {
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
        Item item3 = Item.builder()
                .available(true).owner(user1).description("descr3").name("name3").build();
        itemRepository.save(item3);

        Booking booking1 = Booking.builder() //действующая
                .item(item1).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking1);
        Booking booking2 = Booking.builder() // отмененная
                .item(item1).booker(user3)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking2);
        Booking booking3 = Booking.builder() // итем2
                .item(item2).booker(user1)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking3);
        Booking booking4 = Booking.builder() // в прошлом
                .item(item2).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking4);
        List<Booking> actualBookings = bookingRepository
                .findAllUserItemsFutureBookings(user1.getId(), PageRequest.of(0, 10));
        assertNotNull(actualBookings);
        assertEquals(actualBookings.size(), 0);
    }

    @Test
    @Transactional
    void findAllUserItemsWaitingBookings() {
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
        Item item3 = Item.builder()
                .available(true).owner(user1).description("descr3").name("name3").build();
        itemRepository.save(item3);

        Booking booking1 = Booking.builder() //действующая
                .item(item1).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking1);
        Booking booking2 = Booking.builder() //подходит
                .item(item3).booker(user2)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.WAITING)
                .build();
        bookingRepository.save(booking2);
        Booking booking3 = Booking.builder() // отказанная
                .item(item1).booker(user3)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking3);
        Booking booking4 = Booking.builder() // итем2
                .item(item2).booker(user1)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.WAITING)
                .build();
        bookingRepository.save(booking4);
        Booking booking5 = Booking.builder() // подходит
                .item(item1).booker(user2)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.WAITING)
                .build();
        bookingRepository.save(booking5);
        List<Booking> actualBookings = bookingRepository
                .findAllUserItemsWaitingBookings(user1.getId(), PageRequest.of(0, 10));
        assertNotNull(actualBookings);
        assertEquals(actualBookings.size(), 2);
        assertTrue(actualBookings.contains(booking2));
        assertTrue(actualBookings.contains(booking5));
    }

    @Test
    @Transactional
    void findAllUserItemsWaitingBookings_shouldReturnEmptyList() {
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
        Item item3 = Item.builder()
                .available(true).owner(user1).description("descr3").name("name3").build();
        itemRepository.save(item3);

        Booking booking1 = Booking.builder() //действующая
                .item(item1).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking1);
        Booking booking2 = Booking.builder() //подходит
                .item(item3).booker(user2)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.CANCELED)
                .build();
        bookingRepository.save(booking2);
        Booking booking3 = Booking.builder() // отказанная
                .item(item1).booker(user3)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking3);
        Booking booking4 = Booking.builder() // итем2
                .item(item2).booker(user1)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.WAITING)
                .build();
        bookingRepository.save(booking4);
        Booking booking5 = Booking.builder() // одобренная
                .item(item1).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking5);
        List<Booking> actualBookings = bookingRepository
                .findAllUserItemsWaitingBookings(user1.getId(), PageRequest.of(0, 10));
        assertNotNull(actualBookings);
        assertEquals(actualBookings.size(), 0);
    }

    @Test
    @Transactional
    void findAllUserItemsRejectedBookings() {
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
        Item item3 = Item.builder()
                .available(true).owner(user1).description("descr3").name("name3").build();
        itemRepository.save(item3);

        Booking booking1 = Booking.builder() //действующая
                .item(item1).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking1);
        Booking booking2 = Booking.builder() //подходит
                .item(item3).booker(user2)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking2);
        Booking booking3 = Booking.builder() // отмененная
                .item(item1).booker(user3)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.CANCELED)
                .build();
        bookingRepository.save(booking3);
        Booking booking4 = Booking.builder() // итем2
                .item(item2).booker(user1)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking4);
        Booking booking5 = Booking.builder() // подходит
                .item(item1).booker(user2)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking5);
        List<Booking> actualBookings = bookingRepository
                .findAllUserItemsRejectedBookings(user1.getId(), PageRequest.of(0, 10));
        assertNotNull(actualBookings);
        assertEquals(actualBookings.size(), 2);
        assertTrue(actualBookings.contains(booking2));
        assertTrue(actualBookings.contains(booking5));
    }

    @Test
    @Transactional
    void findAllUserItemsRejectedBookings_shouldReturnEmptyList() {
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
        Item item3 = Item.builder()
                .available(true).owner(user1).description("descr3").name("name3").build();
        itemRepository.save(item3);

        Booking booking1 = Booking.builder() //действующая
                .item(item1).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking1);
        Booking booking2 = Booking.builder() // отмененная
                .item(item1).booker(user3)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.CANCELED)
                .build();
        bookingRepository.save(booking2);
        Booking booking3 = Booking.builder() // итем2
                .item(item2).booker(user1)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking3);
        Booking booking4 = Booking.builder() // одобренная
                .item(item1).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking4);
        List<Booking> actualBookings = bookingRepository
                .findAllUserItemsRejectedBookings(user1.getId(), PageRequest.of(0, 10));
        assertNotNull(actualBookings);
        assertEquals(actualBookings.size(), 0);
    }

    @Test
    @Transactional
    void findAllUserCurrentBookings() {
        User user1 = User.builder().name("name1").email("email1@mail.ru").build();
        userRepository.save(user1);
        User user2 = User.builder().name("name2").email("email2@mail.ru").build();
        userRepository.save(user2);
        User user3 = User.builder().name("name3").email("email3@mail.ru").build();
        userRepository.save(user3);

        Item item1 = Item.builder()
                .available(true).owner(user2).description("descr1").name("name1").build();
        itemRepository.save(item1);
        Item item2 = Item.builder()
                .available(true).owner(user3).description("descr2").name("name2").build();
        itemRepository.save(item2);

        Booking booking1 = Booking.builder() //подходит
                .item(item1).booker(user1)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking1);
        Booking booking2 = Booking.builder() //подходит
                .item(item2).booker(user1)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking2);
        Booking booking3 = Booking.builder() // юзер2
                .item(item2).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking3);
        Booking booking4 = Booking.builder() // юзер3
                .item(item1).booker(user3)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking4);
        Booking booking5 = Booking.builder() // прошедший
                .item(item1).booker(user1)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking5);
        Booking booking6 = Booking.builder() // будущий
                .item(item2).booker(user1)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking6);

        List<Booking> actualBookings = bookingRepository
                .findAllUserCurrentBookings(user1.getId(), PageRequest.of(0, 10));
        assertNotNull(actualBookings);
        assertEquals(actualBookings.size(), 2);
        assertTrue(actualBookings.contains(booking1));
        assertTrue(actualBookings.contains(booking2));
    }

    @Test
    @Transactional
    void findAllUserCurrentBookings_shouldReturnEmptyList() {
        User user1 = User.builder().name("name1").email("email1@mail.ru").build();
        userRepository.save(user1);
        User user2 = User.builder().name("name2").email("email2@mail.ru").build();
        userRepository.save(user2);
        User user3 = User.builder().name("name3").email("email3@mail.ru").build();
        userRepository.save(user3);

        Item item1 = Item.builder()
                .available(true).owner(user2).description("descr1").name("name1").build();
        itemRepository.save(item1);
        Item item2 = Item.builder()
                .available(true).owner(user3).description("descr2").name("name2").build();
        itemRepository.save(item2);

        Booking booking1 = Booking.builder() // юзер2
                .item(item2).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking1);
        Booking booking2 = Booking.builder() // юзер3
                .item(item1).booker(user3)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking2);
        Booking booking3 = Booking.builder() // прошедший
                .item(item1).booker(user1)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking3);
        Booking booking4 = Booking.builder() // будущий
                .item(item2).booker(user1)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking4);

        List<Booking> actualBookings = bookingRepository
                .findAllUserCurrentBookings(user1.getId(), PageRequest.of(0, 10));
        assertNotNull(actualBookings);
        assertEquals(actualBookings.size(), 0);
    }

    @Test
    @Transactional
    void findAllUserBookings() {
        User user1 = User.builder().name("name1").email("email1@mail.ru").build();
        userRepository.save(user1);
        User user2 = User.builder().name("name2").email("email2@mail.ru").build();
        userRepository.save(user2);
        User user3 = User.builder().name("name3").email("email3@mail.ru").build();
        userRepository.save(user3);

        Item item1 = Item.builder()
                .available(true).owner(user2).description("descr1").name("name1").build();
        itemRepository.save(item1);
        Item item2 = Item.builder()
                .available(true).owner(user3).description("descr2").name("name2").build();
        itemRepository.save(item2);

        Booking booking1 = Booking.builder() //подходит
                .item(item1).booker(user1)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking1);
        Booking booking2 = Booking.builder() //подходит
                .item(item2).booker(user1)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking2);
        Booking booking3 = Booking.builder() // юзер2
                .item(item2).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking3);
        Booking booking4 = Booking.builder() // юзер3
                .item(item1).booker(user3)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking4);
        Booking booking5 = Booking.builder() // подходит
                .item(item1).booker(user1)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking5);
        Booking booking6 = Booking.builder() // подходит
                .item(item2).booker(user1)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking6);

        List<Booking> actualBookings = bookingRepository
                .findAllUserBookings(user1.getId(), PageRequest.of(0, 10));
        assertNotNull(actualBookings);
        assertEquals(actualBookings.size(), 4);
        assertTrue(actualBookings.contains(booking1));
        assertTrue(actualBookings.contains(booking2));
        assertTrue(actualBookings.contains(booking5));
        assertTrue(actualBookings.contains(booking6));
    }

    @Test
    @Transactional
    void findAllUserBookings_shouldReturnEmptyList() {
        User user1 = User.builder().name("name1").email("email1@mail.ru").build();
        userRepository.save(user1);
        User user2 = User.builder().name("name2").email("email2@mail.ru").build();
        userRepository.save(user2);
        User user3 = User.builder().name("name3").email("email3@mail.ru").build();
        userRepository.save(user3);

        Item item1 = Item.builder()
                .available(true).owner(user2).description("descr1").name("name1").build();
        itemRepository.save(item1);
        Item item2 = Item.builder()
                .available(true).owner(user3).description("descr2").name("name2").build();
        itemRepository.save(item2);

        Booking booking1 = Booking.builder() // юзер3
                .item(item1).booker(user3)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking1);
        Booking booking2 = Booking.builder() // юзер2
                .item(item2).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking2);
        Booking booking3 = Booking.builder() // юзер2
                .item(item2).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking3);
        Booking booking4 = Booking.builder() // юзер3
                .item(item1).booker(user3)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking4);
        Booking booking5 = Booking.builder() // юзер3
                .item(item1).booker(user3)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking5);
        Booking booking6 = Booking.builder() // юзер2
                .item(item2).booker(user2)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking6);

        List<Booking> actualBookings = bookingRepository
                .findAllUserBookings(user1.getId(), PageRequest.of(0, 10));
        assertNotNull(actualBookings);
        assertEquals(actualBookings.size(), 0);
    }

    @Test
    @Transactional
    void findAllUserPastBookings() {
        User user1 = User.builder().name("name1").email("email1@mail.ru").build();
        userRepository.save(user1);
        User user2 = User.builder().name("name2").email("email2@mail.ru").build();
        userRepository.save(user2);
        User user3 = User.builder().name("name3").email("email3@mail.ru").build();
        userRepository.save(user3);

        Item item1 = Item.builder()
                .available(true).owner(user2).description("descr1").name("name1").build();
        itemRepository.save(item1);
        Item item2 = Item.builder()
                .available(true).owner(user3).description("descr2").name("name2").build();
        itemRepository.save(item2);

        Booking booking1 = Booking.builder() //подходит
                .item(item1).booker(user1)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking1);
        Booking booking2 = Booking.builder() //будущий
                .item(item2).booker(user1)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking2);
        Booking booking3 = Booking.builder() // юзер2
                .item(item2).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking3);
        Booking booking4 = Booking.builder() // юзер3
                .item(item1).booker(user3)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking4);
        Booking booking5 = Booking.builder() // действующий
                .item(item1).booker(user1)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking5);
        Booking booking6 = Booking.builder() // отмененный
                .item(item2).booker(user1)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.CANCELED)
                .build();
        bookingRepository.save(booking6);

        List<Booking> actualBookings = bookingRepository
                .findAllUserPastBookings(user1.getId(), PageRequest.of(0, 10));
        assertNotNull(actualBookings);
        assertEquals(actualBookings.size(), 1);
        assertTrue(actualBookings.contains(booking1));
    }

    @Test
    @Transactional
    void findAllUserPastBookings_shouldReturnEmptyList() {
        User user1 = User.builder().name("name1").email("email1@mail.ru").build();
        userRepository.save(user1);
        User user2 = User.builder().name("name2").email("email2@mail.ru").build();
        userRepository.save(user2);
        User user3 = User.builder().name("name3").email("email3@mail.ru").build();
        userRepository.save(user3);

        Item item1 = Item.builder()
                .available(true).owner(user2).description("descr1").name("name1").build();
        itemRepository.save(item1);
        Item item2 = Item.builder()
                .available(true).owner(user3).description("descr2").name("name2").build();
        itemRepository.save(item2);

        Booking booking1 = Booking.builder() // отмененный
                .item(item2).booker(user1)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.CANCELED)
                .build();
        bookingRepository.save(booking1);
        Booking booking2 = Booking.builder() // будущий
                .item(item2).booker(user1)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking2);
        Booking booking3 = Booking.builder() // юзер2
                .item(item2).booker(user2)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking3);
        Booking booking4 = Booking.builder() // юзер3
                .item(item1).booker(user3)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().minusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking4);
        Booking booking5 = Booking.builder() // действующий
                .item(item1).booker(user1)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking5);

        List<Booking> actualBookings = bookingRepository
                .findAllUserPastBookings(user1.getId(), PageRequest.of(0, 10));
        assertNotNull(actualBookings);
        assertEquals(actualBookings.size(), 0);
    }

    @Test
    @Transactional
    void findAllUserFutureBookings() {
        User user1 = User.builder().name("name1").email("email1@mail.ru").build();
        userRepository.save(user1);
        User user2 = User.builder().name("name2").email("email2@mail.ru").build();
        userRepository.save(user2);
        User user3 = User.builder().name("name3").email("email3@mail.ru").build();
        userRepository.save(user3);

        Item item1 = Item.builder()
                .available(true).owner(user2).description("descr1").name("name1").build();
        itemRepository.save(item1);
        Item item2 = Item.builder()
                .available(true).owner(user3).description("descr2").name("name2").build();
        itemRepository.save(item2);

        Booking booking1 = Booking.builder() // подходит
                .item(item1).booker(user1)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking1);
        Booking booking2 = Booking.builder() // прошлый
                .item(item2).booker(user1)
                .start(LocalDateTime.now().minusWeeks(2))
                .end(LocalDateTime.now().minusWeeks(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking2);
        Booking booking3 = Booking.builder() // юзер2
                .item(item2).booker(user2)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking3);
        Booking booking4 = Booking.builder() // юзер3
                .item(item1).booker(user3)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking4);
        Booking booking5 = Booking.builder() // действующий
                .item(item1).booker(user1)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking5);
        Booking booking6 = Booking.builder() // отмененный
                .item(item2).booker(user1)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.CANCELED)
                .build();
        bookingRepository.save(booking6);

        List<Booking> actualBookings = bookingRepository
                .findAllUserFutureBookings(user1.getId(), PageRequest.of(0, 10));
        assertNotNull(actualBookings);
        assertEquals(actualBookings.size(), 1);
        assertTrue(actualBookings.contains(booking1));
    }

    @Test
    @Transactional
    void findAllUserFutureBookings_shouldReturnEmptyList() {
        User user1 = User.builder().name("name1").email("email1@mail.ru").build();
        userRepository.save(user1);
        User user2 = User.builder().name("name2").email("email2@mail.ru").build();
        userRepository.save(user2);
        User user3 = User.builder().name("name3").email("email3@mail.ru").build();
        userRepository.save(user3);

        Item item1 = Item.builder()
                .available(true).owner(user2).description("descr1").name("name1").build();
        itemRepository.save(item1);
        Item item2 = Item.builder()
                .available(true).owner(user3).description("descr2").name("name2").build();
        itemRepository.save(item2);

        Booking booking1 = Booking.builder() // подходит
                .item(item2).booker(user1)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.CANCELED)
                .build();
        bookingRepository.save(booking1);
        Booking booking2 = Booking.builder() // прошлый
                .item(item2).booker(user1)
                .start(LocalDateTime.now().minusWeeks(2))
                .end(LocalDateTime.now().minusWeeks(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking2);
        Booking booking3 = Booking.builder() // юзер2
                .item(item2).booker(user2)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking3);
        Booking booking4 = Booking.builder() // юзер3
                .item(item1).booker(user3)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking4);
        Booking booking5 = Booking.builder() // действующий
                .item(item1).booker(user1)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking5);

        List<Booking> actualBookings = bookingRepository
                .findAllUserFutureBookings(user1.getId(), PageRequest.of(0, 10));
        assertNotNull(actualBookings);
        assertEquals(actualBookings.size(), 0);
    }

    @Test
    @Transactional
    void findAllUserWaitingBookings() {
        User user1 = User.builder().name("name1").email("email1@mail.ru").build();
        userRepository.save(user1);
        User user2 = User.builder().name("name2").email("email2@mail.ru").build();
        userRepository.save(user2);
        User user3 = User.builder().name("name3").email("email3@mail.ru").build();
        userRepository.save(user3);

        Item item1 = Item.builder()
                .available(true).owner(user2).description("descr1").name("name1").build();
        itemRepository.save(item1);
        Item item2 = Item.builder()
                .available(true).owner(user3).description("descr2").name("name2").build();
        itemRepository.save(item2);

        Booking booking1 = Booking.builder() // подходит
                .item(item1).booker(user1)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.WAITING)
                .build();
        bookingRepository.save(booking1);
        Booking booking2 = Booking.builder() // подходит
                .item(item2).booker(user1)
                .start(LocalDateTime.now().minusWeeks(2))
                .end(LocalDateTime.now().minusWeeks(1))
                .status(Status.WAITING)
                .build();
        bookingRepository.save(booking2);
        Booking booking3 = Booking.builder() // юзер2
                .item(item2).booker(user2)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.WAITING)
                .build();
        bookingRepository.save(booking3);
        Booking booking4 = Booking.builder() // юзер3
                .item(item1).booker(user3)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.WAITING)
                .build();
        bookingRepository.save(booking4);
        Booking booking5 = Booking.builder() // отказанный
                .item(item1).booker(user1)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking5);
        Booking booking6 = Booking.builder() // отмененный
                .item(item2).booker(user1)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.CANCELED)
                .build();
        bookingRepository.save(booking6);

        List<Booking> actualBookings = bookingRepository
                .findAllUserWaitingBookings(user1.getId(), PageRequest.of(0, 10));
        assertNotNull(actualBookings);
        assertEquals(actualBookings.size(), 2);
        assertTrue(actualBookings.contains(booking1));
        assertTrue(actualBookings.contains(booking2));
    }

    @Test
    @Transactional
    void findAllUserWaitingBookings_shouldReturnEmptyList() {
        User user1 = User.builder().name("name1").email("email1@mail.ru").build();
        userRepository.save(user1);
        User user2 = User.builder().name("name2").email("email2@mail.ru").build();
        userRepository.save(user2);
        User user3 = User.builder().name("name3").email("email3@mail.ru").build();
        userRepository.save(user3);

        Item item1 = Item.builder()
                .available(true).owner(user2).description("descr1").name("name1").build();
        itemRepository.save(item1);
        Item item2 = Item.builder()
                .available(true).owner(user3).description("descr2").name("name2").build();
        itemRepository.save(item2);

        Booking booking1 = Booking.builder() // юзер2
                .item(item2).booker(user2)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.WAITING)
                .build();
        bookingRepository.save(booking1);
        Booking booking2 = Booking.builder() // юзер3
                .item(item1).booker(user3)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.WAITING)
                .build();
        bookingRepository.save(booking2);
        Booking booking3 = Booking.builder() // отказанный
                .item(item1).booker(user1)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking3);
        Booking booking4 = Booking.builder() // отмененный
                .item(item2).booker(user1)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.CANCELED)
                .build();
        bookingRepository.save(booking4);

        List<Booking> actualBookings = bookingRepository
                .findAllUserWaitingBookings(user1.getId(), PageRequest.of(0, 10));
        assertNotNull(actualBookings);
        assertEquals(actualBookings.size(), 0);
    }

    @Test
    @Transactional
    void findAllUserRejectedBookings() {
        User user1 = User.builder().name("name1").email("email1@mail.ru").build();
        userRepository.save(user1);
        User user2 = User.builder().name("name2").email("email2@mail.ru").build();
        userRepository.save(user2);
        User user3 = User.builder().name("name3").email("email3@mail.ru").build();
        userRepository.save(user3);

        Item item1 = Item.builder()
                .available(true).owner(user2).description("descr1").name("name1").build();
        itemRepository.save(item1);
        Item item2 = Item.builder()
                .available(true).owner(user3).description("descr2").name("name2").build();
        itemRepository.save(item2);

        Booking booking1 = Booking.builder() // подходит
                .item(item1).booker(user1)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking1);
        Booking booking2 = Booking.builder() // подходит
                .item(item2).booker(user1)
                .start(LocalDateTime.now().minusWeeks(2))
                .end(LocalDateTime.now().minusWeeks(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking2);
        Booking booking3 = Booking.builder() // юзер2
                .item(item2).booker(user2)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking3);
        Booking booking4 = Booking.builder() // юзер3
                .item(item1).booker(user3)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking4);
        Booking booking5 = Booking.builder() // подтвержденный
                .item(item1).booker(user1)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking5);
        Booking booking6 = Booking.builder() // отмененный
                .item(item2).booker(user1)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.CANCELED)
                .build();
        bookingRepository.save(booking6);

        List<Booking> actualBookings = bookingRepository
                .findAllUserRejectedBookings(user1.getId(), PageRequest.of(0, 10));
        assertNotNull(actualBookings);
        assertEquals(actualBookings.size(), 2);
        assertTrue(actualBookings.contains(booking1));
        assertTrue(actualBookings.contains(booking2));
    }

    @Test
    @Transactional
    void findAllUserRejectedBookings_shouldReturnEmptyList() {
        User user1 = User.builder().name("name1").email("email1@mail.ru").build();
        userRepository.save(user1);
        User user2 = User.builder().name("name2").email("email2@mail.ru").build();
        userRepository.save(user2);
        User user3 = User.builder().name("name3").email("email3@mail.ru").build();
        userRepository.save(user3);

        Item item1 = Item.builder()
                .available(true).owner(user2).description("descr1").name("name1").build();
        itemRepository.save(item1);
        Item item2 = Item.builder()
                .available(true).owner(user3).description("descr2").name("name2").build();
        itemRepository.save(item2);

        Booking booking1 = Booking.builder() // юзер2
                .item(item2).booker(user2)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking1);
        Booking booking2 = Booking.builder() // юзер3
                .item(item1).booker(user3)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.REJECTED)
                .build();
        bookingRepository.save(booking2);
        Booking booking3 = Booking.builder() // подтвержденный
                .item(item1).booker(user1)
                .start(LocalDateTime.now().minusMonths(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.APPROVED)
                .build();
        bookingRepository.save(booking3);
        Booking booking4 = Booking.builder() // отмененный
                .item(item2).booker(user1)
                .start(LocalDateTime.now().plusWeeks(2))
                .end(LocalDateTime.now().plusMonths(1))
                .status(Status.CANCELED)
                .build();
        bookingRepository.save(booking4);

        List<Booking> actualBookings = bookingRepository
                .findAllUserRejectedBookings(user1.getId(), PageRequest.of(0, 10));
        assertNotNull(actualBookings);
        assertEquals(actualBookings.size(), 0);
    }
}
