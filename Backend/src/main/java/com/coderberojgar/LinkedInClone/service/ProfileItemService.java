package com.coderberojgar.LinkedInClone.service;

import com.coderberojgar.LinkedInClone.dto.ProfileItemDtos.EducationRequest;
import com.coderberojgar.LinkedInClone.dto.ProfileItemDtos.EducationResponse;
import com.coderberojgar.LinkedInClone.dto.ProfileItemDtos.ExperienceRequest;
import com.coderberojgar.LinkedInClone.dto.ProfileItemDtos.ExperienceResponse;
import com.coderberojgar.LinkedInClone.dto.ProfileItemDtos.SkillRequest;
import com.coderberojgar.LinkedInClone.dto.ProfileItemDtos.SkillResponse;
import java.util.List;

public interface ProfileItemService {

    ExperienceResponse addExperience(ExperienceRequest request);

    ExperienceResponse updateExperience(Long experienceId, ExperienceRequest request);

    void deleteExperience(Long experienceId);

    List<ExperienceResponse> listExperiences(Long userId);

    EducationResponse addEducation(EducationRequest request);

    EducationResponse updateEducation(Long educationId, EducationRequest request);

    void deleteEducation(Long educationId);

    List<EducationResponse> listEducation(Long userId);

    SkillResponse addSkill(SkillRequest request);

    SkillResponse updateSkill(Long skillId, SkillRequest request);

    void deleteSkill(Long skillId);

    List<SkillResponse> listSkills(Long userId);
}
