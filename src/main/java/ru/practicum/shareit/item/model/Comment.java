package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String text;
    @ManyToOne(fetch = FetchType.EAGER)
    private User author;
    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;
    private LocalDateTime timeOfCreated;

    @PrePersist
    private void init() {
        timeOfCreated = LocalDateTime.now();
    }
}
