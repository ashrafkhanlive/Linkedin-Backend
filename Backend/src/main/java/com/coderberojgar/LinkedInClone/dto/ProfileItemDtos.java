package com.coderberojgar.LinkedInClone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public final class ProfileItemDtos {

    private ProfileItemDtos() {
    }

    public record ExperienceRequest(
            @NotBlank String companyName,
            @NotBlank String title,
            String location,
            @NotNull LocalDate startDate,
            LocalDate endDate
    ) {
    }

    public record ExperienceResponse(Long experienceId, Long userId, String companyName, String title, String location, LocalDate startDate, LocalDate endDate) {
    }

    public record EducationRequest(@NotBlank String schoolName, @NotBlank String degree, String fieldOfStudy, @NotNull LocalDate startDate) {
    }

    public record EducationResponse(Long educationId, Long userId, String schoolName, String degree, String fieldOfStudy, LocalDate startDate) {
    }

    public record SkillRequest(@NotBlank String skillName) {
    }

    public record SkillResponse(Long skillId, Long userId, String skillName) {
    }
}
