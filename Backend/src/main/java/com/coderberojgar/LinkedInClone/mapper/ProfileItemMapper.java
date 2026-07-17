package com.coderberojgar.LinkedInClone.mapper;

import com.coderberojgar.LinkedInClone.dto.ProfileItemDtos.EducationRequest;
import com.coderberojgar.LinkedInClone.dto.ProfileItemDtos.EducationResponse;
import com.coderberojgar.LinkedInClone.dto.ProfileItemDtos.ExperienceRequest;
import com.coderberojgar.LinkedInClone.dto.ProfileItemDtos.ExperienceResponse;
import com.coderberojgar.LinkedInClone.dto.ProfileItemDtos.SkillRequest;
import com.coderberojgar.LinkedInClone.dto.ProfileItemDtos.SkillResponse;
import com.coderberojgar.LinkedInClone.entity.Education;
import com.coderberojgar.LinkedInClone.entity.Experience;
import com.coderberojgar.LinkedInClone.entity.Skill;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProfileItemMapper {

    @Mapping(target = "experienceId", ignore = true)
    @Mapping(target = "user", ignore = true)
    Experience toExperience(ExperienceRequest request);

    @Mapping(target = "userId", source = "user.userId")
    ExperienceResponse toExperienceResponse(Experience experience);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "experienceId", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateExperience(ExperienceRequest request, @MappingTarget Experience experience);

    @Mapping(target = "educationId", ignore = true)
    @Mapping(target = "user", ignore = true)
    Education toEducation(EducationRequest request);

    @Mapping(target = "userId", source = "user.userId")
    EducationResponse toEducationResponse(Education education);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "educationId", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEducation(EducationRequest request, @MappingTarget Education education);

    @Mapping(target = "skillId", ignore = true)
    @Mapping(target = "user", ignore = true)
    Skill toSkill(SkillRequest request);

    @Mapping(target = "userId", source = "user.userId")
    SkillResponse toSkillResponse(Skill skill);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "skillId", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateSkill(SkillRequest request, @MappingTarget Skill skill);
}
