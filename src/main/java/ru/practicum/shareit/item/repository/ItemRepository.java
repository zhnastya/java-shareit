package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    @EntityGraph(attributePaths = {"bookings"})
    @Query("SELECT i " +
            " FROM Item as i " +
            " WHERE " +
            " i.available = true" +
            " AND (" +
            " LOWER(i.name) LIKE LOWER(CONCAT('%',:queryText,'%')) " +
            " OR  LOWER(i.description) LIKE LOWER(CONCAT('%',:queryText,'%'))" +
            " )"
    )
    List<Item> findItemByAvailableAndQueryContainWithIgnoreCase(String queryText);

    @EntityGraph(attributePaths = {"bookings"})
    List<Item> findByOwnerIdOrderById(int userId);
}
