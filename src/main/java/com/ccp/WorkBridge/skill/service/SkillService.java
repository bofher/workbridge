package com.ccp.WorkBridge.skill.service;

import com.ccp.WorkBridge.enums.SkillCategory;
import com.ccp.WorkBridge.shared.exceptions.DataNotFoundException;
import com.ccp.WorkBridge.skill.Skill;
import com.ccp.WorkBridge.skill.repo.SkillRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SkillService {
    private SkillRepository skillRepository;

    public List<Skill> findAll() {
        return skillRepository.findAll();
    }

    public Skill findById(Long id) {
        return skillRepository.findById(id).orElseThrow(()-> new DataNotFoundException("Skill not found"));
    }

    public Skill save(Skill skill) {
        return skillRepository.save(skill);
    }

    public List<Skill> findAllByCategory(SkillCategory category) {
        return skillRepository.findAllByCategory(category);
    }

}
