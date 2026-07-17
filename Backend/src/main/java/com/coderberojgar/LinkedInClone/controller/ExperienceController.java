package com.coderberojgar.LinkedInClone.controller;

import com.coderberojgar.LinkedInClone.constant.ApiPaths;
import com.coderberojgar.LinkedInClone.dto.ProfileItemDtos.ExperienceRequest;
import com.coderberojgar.LinkedInClone.dto.ProfileItemDtos.ExperienceResponse;
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
@RequestMapping(ApiPaths.EXPERIENCES)
public class ExperienceController {

    private final ProfileItemService profileItemService;

    public ExperienceController(ProfileItemService profileItemService) {
        this.profileItemService = profileItemService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ExperienceResponse>> add(@Valid @RequestBody ExperienceRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Experience added", profileItemService.addExperience(request)));
    }

    @PutMapping("/{experienceId}")
    public ResponseEntity<ApiResponse<ExperienceResponse>> update(@PathVariable Long experienceId, @Valid @RequestBody ExperienceRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Experience updated", profileItemService.updateExperience(experienceId, request)));
    }

    @DeleteMapping("/{experienceId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long experienceId) {
        profileItemService.deleteExperience(experienceId);
        return ResponseEntity.ok(ApiResponse.success("Experience deleted"));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<List<ExperienceResponse>>> list(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Experiences found", profileItemService.listExperiences(userId)));
    }
}

