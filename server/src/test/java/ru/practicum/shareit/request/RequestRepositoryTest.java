package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class RequestRepositoryTest {

    @Autowired
    RequestRepository requestRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private final User user1 = User.builder()
            .name("name")
            .email("email@email.com")
            .build();

    private final User user2 = User.builder()
            .name("name2")
            .email("email2@email.com")
            .build();

    private final Item item = Item.builder()
            .name("name")
            .description("description")
            .available(true)
            .owner(user1)
            .build();

    private final ItemRequest request1 = ItemRequest.builder()
            .description("request description")
            .created(LocalDateTime.now())
            .owner(user1)
            .build();

    private final ItemRequest request2 = ItemRequest.builder()
            .description("request2 description")
            .created(LocalDateTime.now())
            .owner(user2)
            .build();

    @BeforeEach
    public void init() {
        testEntityManager.persist(user1);
        testEntityManager.persist(user2);
        testEntityManager.persist(item);
        testEntityManager.flush();
        requestRepository.save(request1);
        requestRepository.save(request2);
    }

    @Test
    void findAllByRequesterIdOrderByCreated() {
        List<ItemRequest> requests = requestRepository.findAllByOwnerOrderByCreatedAsc(user1);

        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getDescription(), "request description");
    }
}
