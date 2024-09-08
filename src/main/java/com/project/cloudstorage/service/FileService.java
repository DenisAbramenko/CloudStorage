package com.project.cloudstorage.service;

import com.project.cloudstorage.dto.EditFileRequest;
import com.project.cloudstorage.dto.FileDTO;
import com.project.cloudstorage.entity.File;
import com.project.cloudstorage.entity.User;
import com.project.cloudstorage.repository.FileRepository;
import com.project.cloudstorage.repository.UserRepository;
import com.project.cloudstorage.security.ApplicationUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FileService {
    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final String filePath;

    public FileService(UserRepository userRepository,
                           FileRepository fileRepository,
                           @Value("${file.storage.path}") String filePath) {
        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
        this.filePath = filePath;
    }


    public void saveFile(String hash, MultipartFile multipartFile, String fileName, ApplicationUser user) {

        try (InputStream inputStream = multipartFile.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(filePath + "/" + fileName)) {

            inputStream.transferTo(outputStream);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var foundUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalStateException("Пользователь не найден. id: " + user.getId()));

        File file = new File();
        file.setName(fileName);
        file.setSize(file.getSize());
        file.setUploadedDate(LocalDateTime.now());
        file.setUser(foundUser);

        fileRepository.save(file);

    }

    public void deleteFile(String fileName, ApplicationUser user) throws FileNotFoundException {
        File file = fileRepository.findByName(fileName)
                .orElseThrow(() -> new FileNotFoundException("Файл с таким именем не найден: " + fileName));
        if (!file.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Доступ запрещен.У пользователя нет разрешения на удаление файла.");
        }
        try {
            Files.delete(Path.of(filePath, fileName));
            fileRepository.delete(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Resource downloadFile(String fileName, ApplicationUser user) throws IOException {
        File file = fileRepository.findByName(fileName)
                .orElseThrow(() -> new FileNotFoundException("Файл с таким именем не найден: " + fileName));
        if (!file.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Доступ запрещен.У пользователя нет разрешения на скачивание файла.");
        }

        return new FileSystemResource(Paths.get(filePath, fileName));
    }

    public EditFileRequest editFileName(String fileName, EditFileRequest fileRequest, ApplicationUser user) throws IOException {
        File file = fileRepository.findByName(fileName)
                .orElseThrow(() -> new FileNotFoundException("Файл с таким именем не найден: " + fileName));
        if (!file.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Доступ запрещен.У пользователя нет разрешения на изменение файла.");
        }

        java.io.File fileToMove = new java.io.File(filePath, fileName);
        boolean isMoved = fileToMove.renameTo(new java.io.File(filePath, fileRequest.getName()));
        file.setName(fileRequest.getName());
        fileRepository.save(file);
        if (!isMoved) {
            throw new FileSystemException(filePath);
        }
        return fileRequest;
    }

    public List<FileDTO> getAllFiles(Integer limit, ApplicationUser user) {
        var foundUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalStateException("Пользователь не найден. id: " + user.getId()));

        Page<File> files = fileRepository.findByUser(foundUser, PageRequest.of(0, limit));

        return files.getContent().stream()
                .map(fileEntity -> {
                    FileDTO fileDTO = new FileDTO();
                    fileDTO.setId(fileEntity.getId());
                    fileDTO.setSize(fileEntity.getSize());
                    fileDTO.setName(fileEntity.getName());
                    fileDTO.setUploadedDate(fileEntity.getUploadedDate());
                    return fileDTO;
                })
                .toList();
    }


}
