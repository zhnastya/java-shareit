package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByBookerOrderByTimeOfCreatedDesc(User booker);

    List<Booking> findAllByBookerAndStatusOrderByTimeOfCreatedDesc(User booker, Status status);

    @Query(value = "SELECT b " +
            "FROM Booking as b " +
            "WHERE " +
            "b.booker = :booker " +
            "AND :time BETWEEN b.start and b.end "
    )
    List<Booking> findCustomByCurrent(User booker, LocalDateTime time);

    @Query(value = "SELECT b " +
            "FROM Booking as b " +
            "WHERE " +
            "b.booker = :booker " +
            "AND :time > b.end " +
            "order by b.timeOfCreated desc "
    )
    List<Booking> findCustomByPast(User booker, LocalDateTime time);

    @Query(value = "SELECT b " +
            "FROM Booking as b " +
            "WHERE " +
            "b.booker = :booker " +
            "AND :time < b.start " +
            "order by b.timeOfCreated desc "
    )
    List<Booking> findCustomByFuture(User booker, LocalDateTime time);

    @Query(value = "SELECT b " +
            "FROM Booking as b " +
            "WHERE " +
            "b.item.owner = :owner " +
            "AND :time BETWEEN b.start and b.end " +
            "order by b.timeOfCreated desc "
    )
    List<Booking> findCustomByCurrentOwner(User owner, LocalDateTime time);

    @Query(value = "SELECT b " +
            "FROM Booking as b " +
            "WHERE " +
            "b.item.owner = :owner " +
            "AND :time > b.end " +
            "order by b.timeOfCreated desc "
    )
    List<Booking> findCustomByPastOwner(User owner, LocalDateTime time);

    @Query(value = "SELECT b " +
            "FROM Booking as b " +
            "WHERE " +
            "b.item.owner = :owner " +
            "AND :time < b.start " +
            "order by b.timeOfCreated desc "
    )
    List<Booking> findCustomByFutureOwner(User owner, LocalDateTime time);

    @Query(value = "SELECT b " +
            "FROM Booking as b " +
            "WHERE " +
            "b.item.owner = :owner " +
            "AND b.status = :status " +
            "order by b.timeOfCreated desc "
    )
    List<Booking> findCustomByStatusOwner(User owner, Status status);

    @Query(value = "SELECT b " +
            "FROM Booking as b " +
            "WHERE " +
            "b.item.owner = :owner " +
            "order by b.timeOfCreated desc "
    )
    List<Booking> findCustomAllOwner(User owner);
}
