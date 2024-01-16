package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private final User user = User.builder()
            .name("name")
            .email("email@email.com")
            .build();
    private final User author = User.builder()
            .name("name")
            .email("author@email.com")
            .build();
    private final Item item = Item.builder()
            .name("name")
            .description("description")
            .available(true)
            .owner(user)
            .build();

    private final Comment comment = Comment.builder()
            .item(item)
            .author(author)
            .timeOfCreated(LocalDateTime.now())
            .text("comment")
            .build();


    @BeforeEach
    public void init() {
        testEntityManager.persist(user);
        testEntityManager.persist(author);
        testEntityManager.persist(item);
        testEntityManager.flush();
        commentRepository.save(comment);
    }

    @Test
    void contextLoads() {
        assertThat(commentRepository.count())
                .isEqualTo(1);

        assertThat(testEntityManager.getEntityManager()
                .createQuery("SELECT COUNT(*) FROM Comment i", Long.class)
                .getSingleResult())
                .isEqualTo(1L);
    }
}
