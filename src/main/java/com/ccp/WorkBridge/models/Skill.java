package com.ccp.WorkBridge.models;

import com.ccp.WorkBridge.enums.SkillCategory;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "skills")
public class Skill extends BaseEntity {
    private String skillName;
    @Enumerated(EnumType.STRING)
    SkillCategory category;
}
