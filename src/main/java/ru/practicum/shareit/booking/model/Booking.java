package ru.practicum.shareit.booking.model;

import jdk.jfr.Timestamp;
import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking implements Comparable<Booking> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column (name = "start_date")
    @Timestamp()
    LocalDateTime start;
    @Column (name = "end_date")
    @Timestamp
    LocalDateTime end;
    @ManyToOne
    @JoinColumn(name = "item_id")
    Item item;
    @ManyToOne
    @JoinColumn(name = "booker_id")
    User booker;
    @Enumerated(EnumType.STRING)
    Status status;

    @Override
    public int compareTo(Booking o) {
        if (o.getStart().isAfter(this.getStart())) {
            return 1;
        } else if (this.getStart().isAfter(o.getStart())) {
            return -1;
        } else {
            return 0;
        }
    }
}
