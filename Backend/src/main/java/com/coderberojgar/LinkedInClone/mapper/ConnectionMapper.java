package com.coderberojgar.LinkedInClone.mapper;

import com.coderberojgar.LinkedInClone.dto.ConnectionDtos.ConnectionResponse;
import com.coderberojgar.LinkedInClone.entity.Connection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConnectionMapper {

    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "connectionUserId", source = "connectionUser.userId")
    @Mapping(target = "connectionName", source = "connectionUser.name")
    @Mapping(target = "status", source = "connectionStatus")
    ConnectionResponse toResponse(Connection connection);
}
