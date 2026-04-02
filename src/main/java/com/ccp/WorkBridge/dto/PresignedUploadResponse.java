package com.ccp.WorkBridge.dto;

public record PresignedUploadResponse(
        String url,
        String key
){}
