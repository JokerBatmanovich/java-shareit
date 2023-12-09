package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
//@Entity
//@Table(name = "requests")
public class ItemRequest {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
//    @Column
    String description;
//    @ManyToOne
//    @JoinColumn(name = "requestor_id")
    User requestor;

    LocalDateTime created;
}
