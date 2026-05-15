package com.ccp.WorkBridge.user.controller;

import com.ccp.WorkBridge.dto.CustomUserDetails;
import com.ccp.WorkBridge.dto.GenerateUploadUrlRequest;
import com.ccp.WorkBridge.dto.PresignedUploadResponse;
import com.ccp.WorkBridge.enums.FileType;
import com.ccp.WorkBridge.file.FileEntity;
import com.ccp.WorkBridge.file.service.FileService;
import com.ccp.WorkBridge.file.service.FileStorageService;
import com.ccp.WorkBridge.user.User;
import com.ccp.WorkBridge.user.dto.UpdateUserRequest;
import com.ccp.WorkBridge.user.dto.UserResponse;
import com.ccp.WorkBridge.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final FileService fileService;

    @PostMapping("/update")
    public UserResponse updateUser (@RequestBody UpdateUserRequest updateUserRequest,
                                    @AuthenticationPrincipal CustomUserDetails  customUserDetails) {
        return userService.update(customUserDetails.user().getId(),updateUserRequest);
    }

    @GetMapping("/{id}")
    public UserResponse getDetailedProfileInfo(@PathVariable Long id) {
        return userService.getDetailedInfo(id);
    }
    /**
     * Generates a presigned URL for avatar upload.
     *
     * @param request example:
     *                {
     *                  "fileName": "avatar.png",
     *                  "contentType": "image/png"
     *                }
     */
    @PostMapping("/avatar/upload-url")
    public PresignedUploadResponse uploadAvatarUrl (
            @RequestBody GenerateUploadUrlRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return fileService.generateUploadUrl(
                request.fileName(),
                request.contentType(),
                userDetails.user(),
                FileType.AVATAR
        );
    }

    /**
     * Confirms uploaded an avatar file and attaches it to user profile.
     * Returns a download URL for the avatar.
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/avatar/confirm-upload")
    public ResponseEntity<Map<String, String>> confirmUploadAvatar(@RequestParam String key,
                                                                   @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String url = userService.confirmAvatar(key, customUserDetails.user().getId());
        Map<String, String> response = Map.of("downloadUrl", url);
        return ResponseEntity.ok(response);
    }
}
