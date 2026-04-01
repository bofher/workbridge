package com.ccp.WorkBridge.dto;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public record FileDownloadResponse(byte[] data, String contentType, String filename) {

    public ResponseEntity<byte[]> toResponseEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDisposition(
                ContentDisposition.attachment().filename(filename).build()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .body(data);
    }
}
