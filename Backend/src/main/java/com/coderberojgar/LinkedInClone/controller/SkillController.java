package com.coderberojgar.LinkedInClone.controller;

import com.coderberojgar.LinkedInClone.constant.ApiPaths;
import com.coderberojgar.LinkedInClone.dto.ProfileItemDtos.SkillRequest;
import com.coderberojgar.LinkedInClone.dto.ProfileItemDtos.SkillResponse;
import com.coderberojgar.LinkedInClone.response.ApiResponse;
import com.coderberojgar.LinkedInClone.service.ProfileItemService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.SKILLS)
public class SkillController {

    private final ProfileItemService profileItemService;

    public SkillController(ProfileItemService profileItemService) {
        this.profileItemService = profileItemService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SkillResponse>> add(@Valid @RequestBody SkillRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Skill added", profileItemService.addSkill(request)));
    }

    @PutMapping("/{skillId}")
    public ResponseEntity<ApiResponse<SkillResponse>> update(@PathVariable Long skillId, @Valid @RequestBody SkillRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Skill updated", profileItemService.updateSkill(skillId, request)));
    }

    @DeleteMapping("/{skillId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long skillId) {
        profileItemService.deleteSkill(skillId);
        return ResponseEntity.ok(ApiResponse.success("Skill deleted"));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<List<SkillResponse>>> list(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Skills found", profileItemService.listSkills(userId)));
    }
}

