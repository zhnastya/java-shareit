package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "items", schema = "public")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    @ManyToOne(fetch = FetchType.EAGER)
    private User owner;
    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "item")
    private List<Booking> bookings = new ArrayList<>();
    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "item")
    private List<Comment> comments = new ArrayList<>();

    public void saveBooking(Booking booking) {
        this.bookings.add(booking);
        booking.setItem(this);
    }

    public void saveComment(Comment comment) {
        this.comments.add(comment);
        comment.setItem(this);
    }
}
