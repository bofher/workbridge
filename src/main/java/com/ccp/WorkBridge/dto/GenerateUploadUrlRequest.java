package com.ccp.WorkBridge.dto;

public record GenerateUploadUrlRequest(
        String fileName,
        String contentType
) {}
