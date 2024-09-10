package com.project.cloudstorage.controller;

import com.project.cloudstorage.dto.EditFileRequest;
import com.project.cloudstorage.dto.FileDTO;
import com.project.cloudstorage.security.ApplicationUser;
import com.project.cloudstorage.service.FileService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/file")
    public void uploadFile(
            @RequestParam("hash") String hash,
            @RequestParam("file") MultipartFile file,
            @RequestParam("filename") String fileName,
            @AuthenticationPrincipal ApplicationUser user) {
        fileService.saveFile(hash, file, fileName, user);
    }

    @DeleteMapping("/file")
    public void deleteFile(@RequestParam("filename") String fileName,
                           @AuthenticationPrincipal ApplicationUser user) throws FileNotFoundException {
        fileService.deleteFile(fileName, user);
    }

    @GetMapping("/file")
    public Resource downloadFile(@RequestParam("filename") String fileName,
                                 @AuthenticationPrincipal ApplicationUser user) throws IOException {
        return fileService.downloadFile(fileName, user);
    }

    @PutMapping("/file")
    public EditFileRequest editFileName(@RequestParam("filename") String fileName,
                                        @RequestBody EditFileRequest fileRequest,
                                        @AuthenticationPrincipal ApplicationUser user) throws IOException {
        return fileService.editFileName(fileName, fileRequest, user);
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileDTO>> getAllFiles(@RequestParam(defaultValue = "5") Integer limit,
                                                     @AuthenticationPrincipal ApplicationUser user) {
        List<FileDTO> files = fileService.getAllFiles(limit, user);
        return ResponseEntity.ok(files);
    }

//    @GetMapping("/list")
//    public ResponseEntity<List<FileDTO>> getAllFiles(@RequestParam(defaultValue = "5") Integer limit,
//                                                     @AuthenticationPrincipal ApplicationUser user) {
//        List<FileDTO> files = fileService.getAllFiles(limit, user);
//        return new ResponseEntity<>(files, HttpStatus.OK);
//    }

}
