package com.coderberojgar.LinkedInClone.service.impl;

import com.coderberojgar.LinkedInClone.dto.ProfileItemDtos.EducationRequest;
import com.coderberojgar.LinkedInClone.dto.ProfileItemDtos.EducationResponse;
import com.coderberojgar.LinkedInClone.dto.ProfileItemDtos.ExperienceRequest;
import com.coderberojgar.LinkedInClone.dto.ProfileItemDtos.ExperienceResponse;
import com.coderberojgar.LinkedInClone.dto.ProfileItemDtos.SkillRequest;
import com.coderberojgar.LinkedInClone.dto.ProfileItemDtos.SkillResponse;
import com.coderberojgar.LinkedInClone.entity.Education;
import com.coderberojgar.LinkedInClone.entity.Experience;
import com.coderberojgar.LinkedInClone.entity.Skill;
import com.coderberojgar.LinkedInClone.entity.User;
import com.coderberojgar.LinkedInClone.exception.BadRequestException;
import com.coderberojgar.LinkedInClone.exception.ResourceNotFoundException;
import com.coderberojgar.LinkedInClone.mapper.ProfileItemMapper;
import com.coderberojgar.LinkedInClone.repository.EducationRepository;
import com.coderberojgar.LinkedInClone.repository.ExperienceRepository;
import com.coderberojgar.LinkedInClone.repository.SkillRepository;
import com.coderberojgar.LinkedInClone.service.ProfileItemService;
import com.coderberojgar.LinkedInClone.service.UserService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProfileItemServiceImpl implements ProfileItemService {

    private final ExperienceRepository experienceRepository;
    private final EducationRepository educationRepository;
    private final SkillRepository skillRepository;
    private final UserService userService;
    private final ProfileItemMapper mapper;

    public ProfileItemServiceImpl(ExperienceRepository experienceRepository,
                                  EducationRepository educationRepository,
                                  SkillRepository skillRepository,
                                  UserService userService,
                                  ProfileItemMapper mapper) {
        this.experienceRepository = experienceRepository;
        this.educationRepository = educationRepository;
        this.skillRepository = skillRepository;
        this.userService = userService;
        this.mapper = mapper;
    }

    @Override
    public ExperienceResponse addExperience(ExperienceRequest request) {
        Experience experience = mapper.toExperience(request);
        experience.setUser(userService.currentUser());
        return mapper.toExperienceResponse(experienceRepository.save(experience));
    }

    @Override
    public ExperienceResponse updateExperience(Long experienceId, ExperienceRequest request) {
        Experience experience = ownExperience(experienceId);
        mapper.updateExperience(request, experience);
        return mapper.toExperienceResponse(experience);
    }

    @Override
    public void deleteExperience(Long experienceId) {
        Experience experience = ownExperience(experienceId);
        experience.setDeleted(true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExperienceResponse> listExperiences(Long userId) {
        return experienceRepository.findByUserUserIdAndDeletedFalseOrderByStartDateDesc(userId).stream()
                .map(mapper::toExperienceResponse)
                .toList();
    }

    @Override
    public EducationResponse addEducation(EducationRequest request) {
        Education education = mapper.toEducation(request);
        education.setUser(userService.currentUser());
        return mapper.toEducationResponse(educationRepository.save(education));
    }

    @Override
    public EducationResponse updateEducation(Long educationId, EducationRequest request) {
        Education education = ownEducation(educationId);
        mapper.updateEducation(request, education);
        return mapper.toEducationResponse(education);
    }

    @Override
    public void deleteEducation(Long educationId) {
        Education education = ownEducation(educationId);
        education.setDeleted(true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EducationResponse> listEducation(Long userId) {
        return educationRepository.findByUserUserIdAndDeletedFalseOrderByStartDateDesc(userId).stream()
                .map(mapper::toEducationResponse)
                .toList();
    }

    @Override
    public SkillResponse addSkill(SkillRequest request) {
        User user = userService.currentUser();
        if (skillRepository.existsByUserUserIdAndSkillNameIgnoreCase(user.getUserId(), request.skillName())) {
            throw new BadRequestException("Skill already exists");
        }
        Skill skill = mapper.toSkill(request);
        skill.setUser(user);
        return mapper.toSkillResponse(skillRepository.save(skill));
    }

    @Override
    public SkillResponse updateSkill(Long skillId, SkillRequest request) {
        Skill skill = ownSkill(skillId);
        mapper.updateSkill(request, skill);
        return mapper.toSkillResponse(skill);
    }

    @Override
    public void deleteSkill(Long skillId) {
        Skill skill = ownSkill(skillId);
        skill.setDeleted(true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillResponse> listSkills(Long userId) {
        return skillRepository.findByUserUserIdAndDeletedFalseOrderBySkillNameAsc(userId).stream()
                .map(mapper::toSkillResponse)
                .toList();
    }

    private Experience ownExperience(Long id) {
        Experience experience = experienceRepository.findById(id)
                .filter(item -> !item.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Experience not found"));
        ensureOwner(experience.getUser().getUserId());
        return experience;
    }

    private Education ownEducation(Long id) {
        Education education = educationRepository.findById(id)
                .filter(item -> !item.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Education not found"));
        ensureOwner(education.getUser().getUserId());
        return education;
    }

    private Skill ownSkill(Long id) {
        Skill skill = skillRepository.findById(id)
                .filter(item -> !item.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));
        ensureOwner(skill.getUser().getUserId());
        return skill;
    }

    private void ensureOwner(Long ownerId) {
        if (!ownerId.equals(userService.currentUser().getUserId())) {
            throw new BadRequestException("You can only modify your own profile items");
        }
    }
}
