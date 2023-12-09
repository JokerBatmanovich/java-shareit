package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.util.List;

public interface JpaBookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByItemId(Long itemId);

    void deleteAllByItemId(Long itemId);

    @Query("update Booking b set b.status = :status where b.id = :bookingId")
    Booking setStatus(Long bookingId, Status status);

    @Query("select b from Booking b where b.booker.id = :userId " +
            "and b.item.id = :itemId " +
            "and b.end < current_timestamp")
    List<Booking> findAllSuccessfulBookings(Long userId, Long itemId);

    @Query("select b from Booking b where b.item.owner.id = :userId " +
            "and b.start < current_timestamp " +
            "and b.end > current_timestamp " +
            "order by b.id")
    List<Booking> findAllUserItemsCurrentBookings(Long userId);

    @Query("select b from Booking b where b.item.owner.id = :userId " +
            "order by b.start desc")
    List<Booking> findAllUserItemsBookings(Long userId);

    @Query("select b from Booking b where b.item.owner.id = :userId " +
            "and b.end < current_timestamp " +
            "and b.status <> 'REJECTED' " +
            "and b.status <>'CANCELED' " +
            "order by b.start desc")
    List<Booking> findAllUserItemsPastBookings(Long userId);

    @Query("select b from Booking b where b.item.owner.id = :userId " +
            "and b.start > current_timestamp   " +
            "and b.status <> 'REJECTED' " +
            "and b.status <>'CANCELED' " +
            "order by b.start desc")
    List<Booking> findAllUserItemsFutureBookings(Long userId);

    @Query("select b from Booking b where b.item.owner.id = :userId " +
            "and b.status = 'WAITING'" +
            "order by b.start desc")
    List<Booking> findAllUserItemsWaitingBookings(Long userId);

    @Query("select b from Booking b where b.item.owner.id = :userId " +
            "and b.status = 'REJECTED'" +
            "order by b.start desc")
    List<Booking> findAllUserItemsRejectedBookings(Long userId);

//------------------------------------------------------------------------

    @Query("select b from Booking b where b.booker.id = :userId " +
            "and b.start < current_timestamp " +
            "and b.end > current_timestamp " +
            "order by b.id")
    List<Booking> findAllUserCurrentBookings(Long userId);

    @Query("select b from Booking b where b.booker.id = :userId " +
            "order by b.start desc")
    List<Booking> findAllUserBookings(Long userId);

    @Query("select b from Booking b where b.booker.id = :userId " +
            "and b.end < current_timestamp " +
            "and b.status <> 'REJECTED' " +
            "and b.status <> 'CANCELED' " +
            "order by b.start desc")
    List<Booking> findAllUserPastBookings(Long userId);

    @Query("select b from Booking b where b.booker.id = :userId " +
            "and b.start > current_timestamp   " +
            "and b.status <> 'REJECTED' " +
            "and b.status <> 'CANCELED' " +
            "order by b.start desc")
    List<Booking> findAllUserFutureBookings(Long userId);

    @Query("select b from Booking b where b.booker.id = :userId " +
            "and b.status = 'WAITING'" +
            "order by b.start desc")
    List<Booking> findAllUserWaitingBookings(Long userId);

    @Query("select b from Booking b where b.booker.id = :userId " +
            "and b.status = 'REJECTED'" +
            "order by b.start desc")
    List<Booking> findAllUserRejectedBookings(Long userId);
}
