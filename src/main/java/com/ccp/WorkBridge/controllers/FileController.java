package com.ccp.WorkBridge.controllers;

import com.ccp.WorkBridge.services.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {
    private final FileStorageService storageService;

    @Operation(summary = "Upload a file to S3 storage")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(
            @Parameter(description = "File to upload")
            @RequestPart("file") MultipartFile file) {

        String key = storageService.upload(file);
        return ResponseEntity.ok(key);
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam String key) {
        return storageService.downloadFile(key).toResponseEntity();
    }
}
