package com.project.cloudstorage.dto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FileDTO {
    private Long id;
    private String name;
    private Long size;
    private LocalDateTime uploadedDate;

}
