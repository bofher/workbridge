package com.ccp.WorkBridge.user.mapper;

import com.ccp.WorkBridge.file.service.FileStorageService;
import com.ccp.WorkBridge.user.User;
import com.ccp.WorkBridge.user.dto.UserResponse;

public class UserMapper {

    public static UserResponse toResponse(
            User user,
            FileStorageService storageService
    ) {

        String avatarUrl = null;

        if (user.getAvatarUrl() != null) {
            avatarUrl = storageService.generateDownloadUrl(
                    user.getAvatarUrl()
            );
        }

        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                avatarUrl,
                user.getPhoneNumber(),
                user.getTimeZone() != null
                        ? user.getTimeZone().getId()
                        : null
        );
    }
}
