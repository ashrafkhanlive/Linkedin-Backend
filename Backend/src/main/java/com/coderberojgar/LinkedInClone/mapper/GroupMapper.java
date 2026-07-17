package com.coderberojgar.LinkedInClone.mapper;

import com.coderberojgar.LinkedInClone.dto.GroupDtos.GroupResponse;
import com.coderberojgar.LinkedInClone.entity.LinkedInGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GroupMapper {

    @Mapping(target = "ownerId", source = "user.userId")
    @Mapping(target = "memberCount", expression = "java(group.getMembers() == null ? 0L : group.getMembers().size())")
    GroupResponse toResponse(LinkedInGroup group);
}
