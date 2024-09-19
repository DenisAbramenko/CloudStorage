package com.project.cloudstorage.service;

import com.project.cloudstorage.dto.FileDTO;
import com.project.cloudstorage.entity.File;
import com.project.cloudstorage.entity.User;
import com.project.cloudstorage.repository.FileRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Service
public class FileService {
    private final FileRepository fileRepository;
    private final String filePath;
    private final JwtService jwtService;
    private final UserService userService;

    public FileService(FileRepository fileRepository,
                       @Value("${file.storage.path}") String filePath, JwtService jwtService, UserService userService) {
        this.fileRepository = fileRepository;
        this.filePath = filePath;
        this.jwtService = jwtService;
        this.userService = userService;
    }


    public void saveFile(MultipartFile multipartFile, String fileName, String filePath) {

        try (InputStream inputStream = multipartFile.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(filePath + "/" + fileName)) {

            inputStream.transferTo(outputStream);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        File file = new File();
        file.setName(fileName);
        file.setSize(multipartFile.getSize());
        file.setUploadedDate(LocalDateTime.now());
        file.setUser(userService.getCurrentUser());

        fileRepository.save(file);

    }

    public void deleteFile(String fileName) throws FileNotFoundException {
        File file = fileRepository.findByName(fileName)
                .orElseThrow(() -> new FileNotFoundException("Файл с таким именем не найден: " + fileName));
        if (!file.getUser().getId().equals(userService.getCurrentUser().getId())) {
            throw new AccessDeniedException("Доступ запрещен.У пользователя нет разрешения на удаление файла.");
        }
        java.io.File deleteFile = new java.io.File(filePath, file.getName());
        if (deleteFile.exists()) {
            deleteFile.delete();
        }
        fileRepository.delete(file);
    }

    public Resource downloadFile(String fileName) throws IOException {
        File file = fileRepository.findByName(fileName)
                .orElseThrow(() -> new FileNotFoundException("Файл с таким именем не найден: " + fileName));
        if (!file.getUser().getId().equals(userService.getCurrentUser().getId())) {
            throw new AccessDeniedException("Доступ запрещен.У пользователя нет разрешения на скачивание файла.");
        }

        return new FileSystemResource(Paths.get(filePath, fileName));
    }

    public void editFileName(String oldFileName, Map<String, String> fileName) {
        String newFilename = fileName.get("filename");
        User user = userService.getCurrentUser();
        File file = fileRepository.findByNameAndUser(oldFileName, user)
                .orElseThrow(() -> new FileSystemNotFoundException("File can't be found for user:  "
                        + user.getLogin()
                ));
        file.setName(newFilename);
        fileRepository.saveAndFlush(file);
    }

    public List<FileDTO> getAllFiles(String authToken, Integer limit) throws IOException {
        final String token = authToken.split(" ")[1];
        final String username = jwtService.extractUserName(token);
        final User user = userService.getByUsername(username);
        List<File> files = fileRepository.findAllByUser(user);
        if (files == null) {
            throw new IOException("Files not found");
        }
        return files.stream()
                .limit(limit)
                .map(o -> new FileDTO(o.getName(), o.getSize()))
                .collect(Collectors.toList());
    }
}
