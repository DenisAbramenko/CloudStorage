package com.project.cloudstorage.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "token")
@Data
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "token")
    private String token;

    @Column(name = "loggedOut")
    private boolean loggedOut;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
