package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    @Column(name = "email", unique = true)
    private String email;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL,
            mappedBy = "owner")
    private List<Item> items = new ArrayList<>();
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL,
            mappedBy = "booker")
    private List<Booking> bookings = new ArrayList<>();
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL,
            mappedBy = "author")
    private List<Comment> comments;

    public void saveBooking(Booking booking) {
        this.bookings.add(booking);
    }

    public void saveComment(Comment comment) {
        this.comments.add(comment);
        comment.setAuthor(this);
    }
}
