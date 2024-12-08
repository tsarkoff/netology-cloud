package ru.netology.cloudservice.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.netology.cloudservice.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findItemByOwnerAndFileFilename(String owner, String filename);

    List<Item> findItemByOwnerOrderByFileSizeAsc(String owner, Limit limitOf);

    @Modifying
    @Transactional
    @Query(
            nativeQuery = true,
            value = "INSERT INTO items (filename, size, hash, owner, data) VALUES (:filename, :size, :hash, :owner, :data)")
    int saveItemToDatabase(
            @Param("filename") String filename,
            @Param("size") long size,
            @Param("hash") int hash,
            @Param("owner") String owner,
            @Param("data") byte[] data);
}
