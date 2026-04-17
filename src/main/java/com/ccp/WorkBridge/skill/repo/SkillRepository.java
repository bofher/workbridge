package com.ccp.WorkBridge.skill.repo;

import com.ccp.WorkBridge.enums.SkillCategory;
import com.ccp.WorkBridge.skill.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findAllByCategory(SkillCategory category);
}
