package com.project.cloudstorage.repository;

import com.project.cloudstorage.entity.File;
import com.project.cloudstorage.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    Optional<File> findByName(String fileName);
    Page<File> findByUser(User user, Pageable pageable);
}
