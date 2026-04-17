package com.ccp.WorkBridge.file.repo;

import com.ccp.WorkBridge.file.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface FileRepository extends JpaRepository<FileEntity, Long> {


    Optional<FileEntity> findByStorageKey(String storageKey);
}

