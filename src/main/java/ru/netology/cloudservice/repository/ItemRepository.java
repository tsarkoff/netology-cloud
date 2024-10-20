package ru.netology.cloudservice.repository;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.cloudservice.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findItemByOwnerAndFileFilename(String owner, String filename);
    List<Item> findItemByOwnerOrderByFileSizeAsc(String owner, Limit limitOf);
}
