package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yml")
class UserRepositoryTest {

    @Autowired
    private JpaUserRepository userRepository;

    @Test
    @Transactional
    void findById() {
        User user1 = User.builder().name("name1").email("email1@mail.ru").build();
        userRepository.save(user1);
        User user2 = User.builder().name("name2").email("email2@mail.ru").build();
        userRepository.save(user2);
        User user3 = User.builder().name("name3").email("email3@mail.ru").build();
        userRepository.save(user3);
        User user4 = User.builder().name("name4").email("email4@mail.ru").build();
        userRepository.save(user4);

        Optional<User> actualUser = userRepository.findById(user2.getId());
        assertTrue(actualUser.isPresent());
        assertEquals(actualUser.get(), user2);
    }

    @Test
    @Transactional
    void findById_shouldReturnNull() {
        User user1 = User.builder().name("name1").email("email1@mail.ru").build();
        userRepository.save(user1);
        User user2 = User.builder().name("name2").email("email2@mail.ru").build();
        userRepository.save(user2);
        User user3 = User.builder().name("name3").email("email3@mail.ru").build();
        userRepository.save(user3);
        User user4 = User.builder().name("name4").email("email4@mail.ru").build();
        userRepository.save(user4);

        Optional<User> actualUser = userRepository.findById(user4.getId() + 1);
        assertFalse(actualUser.isPresent());
    }
}
