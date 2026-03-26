package com.ccp.WorkBridge.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.nio.file.Files;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")

public class User extends BaseEntity {
    private String fullName;
    private String email;
    private String password;
    private String phoneNumber;
    private String avatarUrl;
    private Boolean isVerified;
    private Double priorityCoefficient;
    private ZoneId timeZone;

    @OneToMany(mappedBy = "uploadedBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FileEntity> uploadedFiles = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserSkill> skills = new HashSet<>();

    @OneToMany(mappedBy = "recipient", fetch = FetchType.LAZY)
    private List<OrderReview> receivedReviews = new ArrayList<>();

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<OrderReview> givenReviews = new ArrayList<>();
}
