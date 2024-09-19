package com.project.cloudstorage.repository;

import com.project.cloudstorage.entity.File;
import com.project.cloudstorage.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, String> {
    Optional<File> findByName(String fileName);

    Optional<File> findByNameAndUser(String fileName, User user);

    List<File> findAllByUser(User user);
}
