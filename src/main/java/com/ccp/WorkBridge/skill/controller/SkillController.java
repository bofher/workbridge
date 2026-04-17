package com.ccp.WorkBridge.skill.controller;


import com.ccp.WorkBridge.enums.SkillCategory;
import com.ccp.WorkBridge.skill.Skill;
import com.ccp.WorkBridge.skill.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/skill")
public class SkillController {
    private final SkillService skillService;

    @GetMapping
    public List<Skill> getAll() {
        return skillService.findAll();
    }
    @GetMapping("/{id}")
    public Skill getById(@PathVariable Long id) {
        return skillService.findById(id);
    }
    @GetMapping("/category/{category}")
    public List<Skill> getByCategory(@PathVariable SkillCategory category) {
        return skillService.findAllByCategory(category);
    }
    @PostMapping
    public Skill create(@RequestBody Skill skill) {
        return skillService.save(skill);
    }
}
