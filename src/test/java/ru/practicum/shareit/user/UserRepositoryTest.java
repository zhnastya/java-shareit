package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private final User user = User.builder()
            .name("name")
            .email("email@email.com")
            .build();
    private final User user2 = User.builder()
            .name("name2")
            .email("email@email.com")
            .build();

    @BeforeEach
    public void addItems() {
        testEntityManager.flush();
        userRepository.save(user);
    }

    @Test
    void contextLoads() {
        assertThat(userRepository.count())
                .isEqualTo(1);
        assertThat(testEntityManager.getEntityManager()
                .createQuery("SELECT COUNT(*) FROM User i", Long.class)
                .getSingleResult())
                .isEqualTo(1L);
        assertThrows(DataIntegrityViolationException.class,
                () -> userRepository.save(user2));
    }
}
