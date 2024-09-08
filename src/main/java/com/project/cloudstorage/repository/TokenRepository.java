package com.project.cloudstorage.repository;

import com.project.cloudstorage.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {
    Optional<Token> findByTokenAndLoggedOutEquals(String token, boolean loggedOut);
}
