package com.project.cloudstorage.service;

import com.project.cloudstorage.dto.FileDTO;
import com.project.cloudstorage.entity.File;
import com.project.cloudstorage.entity.User;
import com.project.cloudstorage.repository.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private FileRepository fileRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserService userService;
    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private FileService fileService;

    private User user;
    private File file;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setLogin("testuser");

        file = new File();
        file.setName("testFile.txt");
        file.setSize(12L);
        file.setUser(user);
    }

    @Test
    public void saveFileTest() throws IOException {
        FileRepository fileRepository = Mockito.mock(FileRepository.class);
        String filePath = "src/test/java/com/project/cloudstorage/service/";
        FileService fileService1 = new FileService(fileRepository, filePath, jwtService, userService);
        MultipartFile mockMultipartFile = Mockito.mock(MultipartFile.class);
        File mockFile = Mockito.mock();
        mockFile.setName("testFile.txt");
        mockFile.setSize(12L);
        mockFile.setUser(user);
        String fileName = file.getName();
        when(mockMultipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("test content".getBytes()));
        when(mockMultipartFile.getSize()).thenReturn(12L);
        when(userService.getCurrentUser()).thenReturn(user);
        when(fileRepository.save(any(File.class))).thenReturn(mockFile);

        fileService1.saveFile(mockMultipartFile, fileName, filePath);

        verify(fileRepository, times(1)).save(any(File.class));
        ArgumentCaptor<File> fileCaptor = ArgumentCaptor.forClass(File.class);
        verify(fileRepository).save(fileCaptor.capture());
        File savedFile = fileCaptor.getValue();
        assertThat(savedFile.getFileContent()).isEqualTo(mockFile.getFileContent());
        assertThat(savedFile.getName()).isEqualTo(fileName);
        assertThat(savedFile.getSize()).isEqualTo(12L);
        assertThat(savedFile.getUser()).isEqualTo(user);

//        java.io.File file = new java.io.File(filePath + "/" + fileName);
//        assertThat(file.exists()).isTrue();
//        file.delete();
    }

    @Test
    void deleteFileTest() throws FileNotFoundException {
        String fileName = "testFile.txt";
        when(fileRepository.findByName(fileName)).thenReturn(Optional.ofNullable(file));
        when(userService.getCurrentUser()).thenReturn(user);

        fileService.deleteFile(fileName);

        verify(fileRepository, times(1)).delete(file);
    }

    @Test
    public void downloadFileTest() throws IOException {
        File mockFile = Mockito.mock();
        mockFile.setName("testFile.txt");
        mockFile.setSize(12L);
        mockFile.setUser(user);
        mockFile.setFileContent("testFile.txt".getBytes());
        String fileName = file.getName();
        when(fileRepository.findByName(fileName)).thenReturn(Optional.of(mockFile));
        when(mockFile.getUser()).thenReturn(user);
        when(userService.getCurrentUser()).thenReturn(user);
        String filePath = "src/test/java/com/project/cloudstorage/service/";
        FileService fileService = new FileService(fileRepository, filePath, jwtService, userService);

        byte[] fileData = fileService.downloadFile(fileName).getContentAsByteArray();

        assertThat(fileData).isNotNull();
        assertThat(fileData.length).isEqualTo(12L);
    }

    @Test
    public void editFileNameTest() {
        String oldFileName = "old.txt";
        String newFileName = "new.txt";
        Map<String, String> fileName = new HashMap<>();
        fileName.put("filename", newFileName);
        when(userService.getCurrentUser()).thenReturn(user);
        when(fileRepository.findByNameAndUser(oldFileName, user)).thenReturn(Optional.ofNullable(file));

        fileService.editFileName(oldFileName, fileName);

        verify(fileRepository, times(1)).saveAndFlush(file);
        assertEquals(newFileName, file.getName());
    }

    @Test
    void getAllFiles_shouldReturnListOfFileDTOs() throws IOException {
        when(jwtService.extractUserName(any())).thenReturn("testuser");
        when(userService.getByUsername("testuser")).thenReturn(user);
        when(fileRepository.findAllByUser(user)).thenReturn(Collections.singletonList(file));

        List<FileDTO> fileDTO = fileService.getAllFiles("Bearer token", 10);

        assertNotNull(fileDTO);
        assertEquals(1, fileDTO.size());
        assertEquals(file.getName(), fileDTO.get(0).getFilename());
        assertEquals(12L, fileDTO.get(0).getSize());
    }
}
