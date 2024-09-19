package com.project.cloudstorage.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NonNull
public class FileDTO {
    private String filename;
    private Long size;
}
