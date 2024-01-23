package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private final User user = User.builder()
            .name("name")
            .email("email@email.com")
            .build();

    private final Item item = Item.builder()
            .name("name")
            .description("description")
            .available(true)
            .owner(user)
            .build();


    @BeforeEach
    public void addItems() {
        testEntityManager.persist(user);
        testEntityManager.flush();
        itemRepository.save(item);
    }

    @Test
    void contextLoads() {
        assertThat(itemRepository.count())
                .isEqualTo(1);

        assertThat(testEntityManager.getEntityManager()
                .createQuery("SELECT COUNT(*) FROM Item i", Long.class)
                .getSingleResult())
                .isEqualTo(1L);
    }
}
