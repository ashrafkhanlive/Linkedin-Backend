package com.coderberojgar.LinkedInClone.mapper;

import com.coderberojgar.LinkedInClone.dto.ProfileDtos.ProfileRequest;
import com.coderberojgar.LinkedInClone.dto.ProfileDtos.ProfileResponse;
import com.coderberojgar.LinkedInClone.entity.Profile;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProfileMapper {

    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "name", source = "user.name")
    ProfileResponse toResponse(Profile profile);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(ProfileRequest request, @MappingTarget Profile profile);
}
