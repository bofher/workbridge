package com.ccp.WorkBridge.user.dto;

import com.ccp.WorkBridge.user.User;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        String avatarUrl,
        String phoneNumber,
        String timeZone
) {
}
