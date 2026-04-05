package com.ccp.WorkBridge.repos;

import com.ccp.WorkBridge.enums.SkillCategory;
import com.ccp.WorkBridge.models.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findAllByCategory(SkillCategory category);
}
