package com.coderberojgar.LinkedInClone.mapper;

import com.coderberojgar.LinkedInClone.constant.RoleName;
import com.coderberojgar.LinkedInClone.dto.UserDtos.UserResponse;
import com.coderberojgar.LinkedInClone.entity.User;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "roles", expression = "java(toRoleNames(user.getRoles()))")
    UserResponse toResponse(User user);

    default Set<String> toRoleNames(Set<RoleName> roles) {
        return roles.stream().map(Enum::name).collect(Collectors.toSet());
    }
}
