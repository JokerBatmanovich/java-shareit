package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Set;

public interface JpaItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerIdOrderById(Long ownerId, Pageable page);

    List<Item> findAllByRequestIdIn(Set<Long> ids);

    List<Item> findAllByRequestId(Long id);

    @Query("select i from Item i where i.available is TRUE " +
            "and (upper(i.description) like upper(concat('%', ?1, '%')) " +
            "or upper(i.name) like upper(concat('%', ?1, '%')))")
    List<Item> search(String text, Pageable page);
}
