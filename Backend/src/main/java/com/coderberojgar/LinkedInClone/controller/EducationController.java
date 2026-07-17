package com.coderberojgar.LinkedInClone.controller;


import com.coderberojgar.LinkedInClone.constant.ApiPaths;
import com.coderberojgar.LinkedInClone.dto.ProfileItemDtos.EducationRequest;
import com.coderberojgar.LinkedInClone.dto.ProfileItemDtos.EducationResponse;
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
@RequestMapping(ApiPaths.EDUCATION)
public class EducationController {

    private final ProfileItemService profileItemService;

    public EducationController(ProfileItemService profileItemService) {
        this.profileItemService = profileItemService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EducationResponse>> add(@Valid @RequestBody EducationRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Education added", profileItemService.addEducation(request)));
    }

    @PutMapping("/{educationId}")
    public ResponseEntity<ApiResponse<EducationResponse>> update(@PathVariable Long educationId, @Valid @RequestBody EducationRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Education updated", profileItemService.updateEducation(educationId, request)));
    }

    @DeleteMapping("/{educationId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long educationId) {
        profileItemService.deleteEducation(educationId);
        return ResponseEntity.ok(ApiResponse.success("Education deleted"));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<List<EducationResponse>>> list(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Education found", profileItemService.listEducation(userId)));
    }
}

