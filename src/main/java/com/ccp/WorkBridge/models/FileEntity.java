package com.ccp.WorkBridge.models;


import com.ccp.WorkBridge.enums.FileType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "files")
public class FileEntity extends BaseEntity {

    private String storageKey;
    private String originalName;
    private String mimeType;
    private Long sizeBytes;

    @Enumerated(EnumType.STRING)
    private FileType fileType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;
    private boolean confirmed = false;

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MessageFile> messageFiles = new HashSet<>();

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderFile> orderFiles = new HashSet<>();

    private Instant uploadedAt;
    private Instant deletedAt;

}
