package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByBooker(User booker, Pageable pageable);

    List<Booking> findAllByItem_IdIn(List<Integer> id);

    @Query(value = "SELECT b " +
            "FROM Booking as b " +
            "WHERE " +
            "b.id = :bookingId " +
            "AND b.item.owner = :owner "
    )
    Optional<Booking> findCustomByOwnerAndBookingId(User owner, int bookingId);

    @Query(value = "SELECT b " +
            "FROM Booking as b " +
            "WHERE " +
            "b.id = :bookingId " +
            "AND (" +
            "b.item.owner = :user " +
            "OR b.booker = :user " +
            ")"
    )
    Optional<Booking> findCustomAnyUserAndBookingId(User user, int bookingId);

    @Query(value = "SELECT b " +
            "FROM Booking as b " +
            "WHERE " +
            "b.booker = :booker " +
            "AND b.item = :item " +
            "AND b.status = :status " +
            "AND b.end < :time "
    )
    List<Booking> findAllByBookerAndItemAndStatus(User booker, Item item, Status status, LocalDateTime time);

    List<Booking> findAllByBookerAndStatus(User booker, Status status, Pageable pageable);

    @Query(value = "SELECT b " +
            "FROM Booking as b " +
            "WHERE " +
            "b.booker = :booker " +
            "AND :time BETWEEN b.start and b.end " +
            "order by b.start desc"
    )
    List<Booking> findCustomByCurrent(User booker, LocalDateTime time, Pageable pageable);

    @Query(value = "SELECT b " +
            "FROM Booking as b " +
            "WHERE " +
            "b.booker = :booker " +
            "AND :time > b.end " +
            "order by b.start desc"
    )
    List<Booking> findCustomByPast(User booker, LocalDateTime time, Pageable pageable);

    @Query(value = "SELECT b " +
            "FROM Booking as b " +
            "WHERE " +
            "b.booker = :booker " +
            "AND :time < b.start " +
            "order by b.start desc"
    )
    List<Booking> findCustomByFuture(User booker, LocalDateTime time, Pageable pageable);

    @Query(value = "SELECT b " +
            "FROM Booking as b " +
            "WHERE " +
            "b.item.owner = :owner " +
            "AND :time BETWEEN b.start and b.end " +
            "order by b.start desc"
    )
    List<Booking> findCustomByCurrentOwner(User owner, LocalDateTime time, Pageable pageable);

    @Query(value = "SELECT b " +
            "FROM Booking as b " +
            "WHERE " +
            "b.item.owner = :owner " +
            "AND :time > b.end " +
            "order by b.start desc"
    )
    List<Booking> findCustomByPastOwner(User owner, LocalDateTime time, Pageable pageable);

    @Query(value = "SELECT b " +
            "FROM Booking as b " +
            "WHERE " +
            "b.item.owner = :owner " +
            "AND :time < b.start " +
            "order by b.start desc"
    )
    List<Booking> findCustomByFutureOwner(User owner, LocalDateTime time, Pageable pageable);

    @Query(value = "SELECT b " +
            "FROM Booking as b " +
            "WHERE " +
            "b.item.owner = :owner " +
            "AND b.status = :status " +
            "order by b.start desc "
    )
    List<Booking> findCustomByStatusOwner(User owner, Status status, Pageable pageable);

    @Query(value = "SELECT b " +
            "FROM Booking as b " +
            "WHERE " +
            "b.item.owner = :owner " +
            "order by b.start DESC "
    )
    List<Booking> findCustomAllOwner(User owner, Pageable pageable);
}
