package com.project.cloudstorage.dto;


import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NonNull
public class FileDTO {
//    private Long id;
    private String filename;
    private Long size;
//    private LocalDateTime uploadedDate;

}
