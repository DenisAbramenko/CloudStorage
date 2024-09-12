package com.project.cloudstorage.service;

import com.project.cloudstorage.dto.FileDTO;
import com.project.cloudstorage.entity.File;
import com.project.cloudstorage.entity.User;
import com.project.cloudstorage.repository.FileRepository;
//import com.project.cloudstorage.security.ApplicationUser;
//import com.project.cloudstorage.security.ApplicationUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FileService {
    private final FileRepository fileRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;
    private final String filePath;

    public FileService(FileRepository fileRepository,
                       @Value("${file.storage.path}") String filePath) {
//        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
        this.filePath = filePath;
    }


    public void saveFile(MultipartFile multipartFile, String fileName) throws IOException {

        try (InputStream inputStream = multipartFile.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(filePath + "/" + fileName)) {

            inputStream.transferTo(outputStream);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        if (user == null) {
//            userService.save(user.);
//        }
//        var foundUser = userRepository.findById(user.getId())
//                .orElseThrow(() -> new IllegalStateException("Пользователь не найден. id: " + user.getId()));

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

    public void editFileName(String oldFileName, Map<String,String> fileName) {
        String newFilename = fileName.get("filename");
        User user = userService.getCurrentUser();
        File file = fileRepository.findByNameAndUser(oldFileName, user)
                .orElseThrow(() -> new FileSystemNotFoundException("File can't be found for user:  "
                        + user.getLogin()
                ));
        file.setName(newFilename);
        fileRepository.saveAndFlush(file);
    }



//    public List<FileDTO> getAllFiles(Integer limit, ApplicationUser user) throws FileNotFoundException {
//        if (user == null){
//            throw new FileNotFoundException();
//        }
//        User foundUser = userRepository.findById(user.getId())
//                .orElseThrow(() -> new IllegalStateException("Пользователь не найден"));
//
//        Page<File> files = fileRepository.findByUser(foundUser, PageRequest.of(0, limit));
//
//        return files.getContent().stream()
//                .map(fileEntity -> {
//                    FileDTO fileDTO = new FileDTO();
//                    fileDTO.setId(fileEntity.getId());
//                    fileDTO.setSize(fileEntity.getSize());
//                    fileDTO.setName(fileEntity.getName());
//                    fileDTO.setUploadedDate(fileEntity.getUploadedDate());
//                    return fileDTO;
//                })
//                .toList();
//    }
//todo limit
    public List<FileDTO> getAllFiles(String authToken, Integer limit) throws IOException {
        final String token = authToken.split(" ")[1];
        final String username = jwtService.extractUserName(token);
        final User user = userService.getByUsername(username);
//        if (user == null) {
//            throw new IOException("Get all files: Unauthorized");
//        }
//        fileRepository.deleteAll();
        List<File> files = fileRepository.findAllByUser(user);
        if (files == null){
            throw new IOException("Files not found");
        }
        return files.stream()
                .limit(limit)
                .map(o -> new FileDTO(o.getName(), o.getSize()))
                .collect(Collectors.toList());
    }
}
