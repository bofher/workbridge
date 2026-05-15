package com.ccp.WorkBridge.user.service;

import com.ccp.WorkBridge.dto.PresignedUploadResponse;
import com.ccp.WorkBridge.file.FileEntity;
import com.ccp.WorkBridge.file.repo.FileRepository;
import com.ccp.WorkBridge.file.service.FileService;
import com.ccp.WorkBridge.file.service.FileStorageService;
import com.ccp.WorkBridge.user.User;
import com.ccp.WorkBridge.user.dto.UpdateUserRequest;
import com.ccp.WorkBridge.user.dto.UserResponse;
import com.ccp.WorkBridge.user.mapper.UserMapper;
import com.ccp.WorkBridge.user.repo.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final FileService fileService;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public UserResponse update(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (request.name() != null) {
            user.setFullName(request.name());
        }
        if (request.email() != null) {
            user.setEmail(request.email());
        }
        if (request.timeZone() != null) {
            user.setTimeZone(request.timeZone());
        }
        if (request.phone() != null) {
            user.setPhoneNumber(request.phone());
        }
        return UserMapper.toResponse(user,fileStorageService);
    }


    public String confirmAvatar(String key, Long userId) {
        FileEntity fileEntity = fileService.confirmUpload(key);
        User user =  userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setAvatarUrl(fileEntity.getStorageKey());
        userRepository.save(user);

        return fileStorageService.generateDownloadUrl(fileEntity.getStorageKey());
    }

    public UserResponse getDetailedInfo(Long id) {
        return UserMapper.toResponse(userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found")),
                fileStorageService);
    }
}
