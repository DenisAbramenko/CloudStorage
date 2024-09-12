package com.project.cloudstorage.controller;

import com.project.cloudstorage.dto.FileDTO;
//import com.project.cloudstorage.security.ApplicationUser;
import com.project.cloudstorage.service.FileService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/file")
    public void uploadFile(
//            @RequestParam("hash") String hash,
            @RequestParam("file") MultipartFile file,
            @RequestParam("filename") String fileName) throws IOException {
        fileService.saveFile(file, fileName);
    }

    @DeleteMapping("/file")
    public void deleteFile(@RequestParam("filename") String fileName) throws FileNotFoundException {
        fileService.deleteFile(fileName);
    }

    @GetMapping("/file")
    public Resource downloadFile(@RequestParam("filename") String fileName) throws IOException {
        return fileService.downloadFile(fileName);
    }

    @PutMapping("/file")
    public ResponseEntity<String> editFileName(@RequestHeader("auth-token") String authToken,
                                               @RequestParam("filename") String oldFileName,
                                               @RequestBody Map<String,String> newFileName) {
        fileService.editFileName(oldFileName, newFileName);
        return new ResponseEntity<>("Success upload", HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileDTO>> getAllFiles(@RequestHeader("auth-token") String authToken,
                                                     @RequestParam("limit") int limit) throws IOException {
        List<FileDTO> files = fileService.getAllFiles(authToken, limit);
        return ResponseEntity.ok(files);
    }

}
