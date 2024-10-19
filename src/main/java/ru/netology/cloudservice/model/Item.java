package ru.netology.cloudservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Embedded
    private Item.File file;

    @Column(name = "hash", nullable = false)
    private int hash;

    @Embeddable
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class File {
        @Column(name = "filename", nullable = false)
        private String filename;
        @Column(name = "size", nullable = false)
        private long size;
    }
}
