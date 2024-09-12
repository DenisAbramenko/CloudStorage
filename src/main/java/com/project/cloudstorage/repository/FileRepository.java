package com.project.cloudstorage.repository;

import com.project.cloudstorage.entity.File;
import com.project.cloudstorage.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Repository
public interface FileRepository extends JpaRepository<File, String> {
    Optional<File> findByName(String fileName);
    Optional<File> findByNameAndUser(String fileName, User user);
    List<File> findAllByUser(User user);
}
