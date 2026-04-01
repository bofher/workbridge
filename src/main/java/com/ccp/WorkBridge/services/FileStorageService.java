package com.ccp.WorkBridge.services;

import com.ccp.WorkBridge.dto.FileDownloadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {
    private final S3Client s3Client;

    @Value("${s3.bucket}")
    private String bucket;

    public String upload(MultipartFile file) {
        try {
            String key = UUID.randomUUID() + "_" + file.getOriginalFilename();

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );
            return key;
        } catch (IOException e) {
            throw new RuntimeException("Upload failed");
        }
    }

    public FileDownloadResponse downloadFile(String key) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(request);
        GetObjectResponse metadata = objectBytes.response();

        String contentType = metadata.contentType() != null && !metadata.contentType().isEmpty()
                ? metadata.contentType()
                : "application/octet-stream";
        String filename = Paths.get(key).getFileName().toString();

        return new FileDownloadResponse(objectBytes.asByteArray(), contentType, filename);
    }
}
