package com.ccp.WorkBridge.services.file;

import com.ccp.WorkBridge.dto.PresignedUploadResponse;
import com.ccp.WorkBridge.enums.FileType;
import com.ccp.WorkBridge.exceptions.DataNotFoundException;
import com.ccp.WorkBridge.models.FileEntity;
import com.ccp.WorkBridge.models.User;
import com.ccp.WorkBridge.repos.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileStorageService storageService;
    private final FileRepository fileRepository;

    public FileEntity upload(MultipartFile file, User user, FileType fileType) {
        String key = storageService.upload(file);

        FileEntity entity = FileEntity.builder()
                .storageKey(key)
                .originalName(file.getOriginalFilename())
                .mimeType(file.getContentType())
                .sizeBytes(file.getSize())
                .fileType(fileType)
                .uploadedBy(user)
                .uploadedAt(Instant.now())
                .build();

        return fileRepository.save(entity);
    }

    public PresignedUploadResponse generateUploadUrl(String originalName, String contentType, User user) {
        PresignedUploadResponse presigned = storageService.generateUploadUrl(originalName, contentType);

        FileEntity file = FileEntity.builder()
                .storageKey(presigned.key())
                .originalName(originalName)
                .mimeType(contentType)
                .uploadedBy(user)
                .confirmed(false)
                .build();

        fileRepository.save(file);

        return presigned;
    }

    public FileEntity confirmUpload(String key) {

        FileEntity file = fileRepository.findByStorageKey(key).orElseThrow(
                () -> new DataNotFoundException("File not found"));
        long size = storageService.validateFileExists(key);

        //TODO filesize limit by subscription with a help of user details

        file.setConfirmed(true);
        file.setSizeBytes(size);
        file.setUploadedAt(Instant.now());
        return fileRepository.save(file);


    }

}
