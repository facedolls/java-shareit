package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;

@Entity
@Table(name = "requests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;
    private String description;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User requester;
    private LocalDateTime created;
    @OneToMany(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "request_id")
    private Collection<Item> items;
}
