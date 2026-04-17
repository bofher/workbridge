package com.ccp.WorkBridge.skill;

import com.ccp.WorkBridge.enums.SkillCategory;
import com.ccp.WorkBridge.shared.BaseEntity;
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
