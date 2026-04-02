package com.ccp.WorkBridge.repos;

import com.ccp.WorkBridge.models.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface FileRepository extends JpaRepository<FileEntity, Long> {


    Optional<FileEntity> findByStorageKey(String storageKey);
}

