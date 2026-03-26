package com.ccp.WorkBridge.models;


import com.ccp.WorkBridge.enums.FileType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "files")
public class FileEntity extends BaseEntity {

    private String storageKey;
    private String originalName;
    private String mimeType;
    private Long sizeBytes;
    private String url;

    @Enumerated(EnumType.STRING)
    private FileType fileType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    private Instant uploadedAt;
    private Instant deletedAt;

}
