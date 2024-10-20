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
    private FileDto file;

    @Column(name = "hash", nullable = false)
    private int hash;

    @Column(name = "owner", nullable = false)
    private String owner;

    @Column(name = "data", nullable = false)
    private byte[] data;

    @Embeddable
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class FileDto {
        @Column(name = "filename", nullable = false)
        private String filename;
        @Column(name = "size", nullable = false)
        private long size;
    }
}
