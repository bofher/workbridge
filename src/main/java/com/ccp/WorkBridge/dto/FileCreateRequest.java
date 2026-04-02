package com.ccp.WorkBridge.dto;

public record FileCreateRequest(
        String key,
        String originalName,
        String mimeType,
        Long size
) {
}
