package com.ccp.WorkBridge.services.file;

import com.ccp.WorkBridge.dto.FileDownloadResponse;
import com.ccp.WorkBridge.dto.PresignedUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {
    private final S3Client s3Client;
    private final S3Presigner presigner;

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

    public PresignedUploadResponse generateUploadUrl(String originalName, String contentType) {

        String key = UUID.randomUUID() + "_" + originalName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest =
                PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(10))
                        .putObjectRequest(putObjectRequest)
                        .build();

        PresignedPutObjectRequest presignedRequest =
                presigner.presignPutObject(presignRequest);

        return new PresignedUploadResponse(
                presignedRequest.url().toString(),
                key
        );
    }

    public String generateDownloadUrl(String key) {
        return presigner.presignGetObject(
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(10))
                        .getObjectRequest(
                                GetObjectRequest.builder()
                                        .bucket(bucket)
                                        .key(key)
                                        .build()
                        )
                        .build()
        ).url().toString();
    }


    public Long validateFileExists(String key) {
        return s3Client.headObject(
                HeadObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build()
        ).contentLength();
    }
}
