package ru.practicum.shareit.user.model;

import lombok.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
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
    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "owner")
    private List<Item> items = new ArrayList<>();
    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "booker")
    private List<Booking> bookings = new ArrayList<>();
    @OneToMany(fetch = FetchType.LAZY,
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
