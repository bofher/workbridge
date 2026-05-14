package com.ccp.WorkBridge.message.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SendMessageRequest(
        @JsonProperty("content")
        String content
) {}
