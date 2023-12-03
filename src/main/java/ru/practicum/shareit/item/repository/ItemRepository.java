package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

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

    List<Item> findByOwnerIdOrderById(int userId);

    @Query(value = "SELECT b.booker " +
            "FROM Item as i " +
            "LEFT join Booking as b on i.id = b.item.id " +
            "WHERE i.id = :itemId " +
            "AND b.status = :status " +
            "AND :time BETWEEN b.start AND b.end " +
            "OR :time > b.end"
    )
    List<User> findCustomStoryBookers(int itemId, LocalDateTime time, Status status);
}
