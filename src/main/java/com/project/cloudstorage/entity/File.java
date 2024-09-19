package com.project.cloudstorage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "files")
@AllArgsConstructor
@NoArgsConstructor
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "filename")
    private String name;
    private Long size;
    private LocalDateTime uploadedDate;
    private byte[] fileContent;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
