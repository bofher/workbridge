package com.ccp.WorkBridge.controllers;

import com.ccp.WorkBridge.dto.CustomUserDetails;
import com.ccp.WorkBridge.dto.GenerateUploadUrlRequest;
import com.ccp.WorkBridge.dto.PresignedUploadResponse;
import com.ccp.WorkBridge.models.FileEntity;
import com.ccp.WorkBridge.services.file.FileService;
import com.ccp.WorkBridge.services.file.FileStorageService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {
    private final FileStorageService storageService;
    private final FileService fileService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(
            @Parameter(description = "File to upload")
            @RequestPart("file") MultipartFile file) {

        String key = storageService.upload(file);
        return ResponseEntity.ok(key);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam String key) {
        return storageService.downloadFile(key).toResponseEntity();
    }

    @PostMapping("/upload-url")
    public PresignedUploadResponse getUploadUrl(
            @RequestBody GenerateUploadUrlRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return fileService.generateUploadUrl(
                request.fileName(),
                request.contentType(),
                userDetails.user()
        );
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/confirm-upload")
    public FileEntity confirmUpload(@RequestParam String key) {
        return fileService.confirmUpload(key);
    }

    @GetMapping("/download-url")
    public ResponseEntity<Map<String, String>> getDownloadUrl(
            @RequestParam String key
    ) {
        String url = storageService.generateDownloadUrl(key);
        Map<String, String> response = Map.of("downloadUrl", url);
        return ResponseEntity.ok(response);
    }
}
