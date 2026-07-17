package com.coderberojgar.LinkedInClone.mapper;

import com.coderberojgar.LinkedInClone.dto.SocialDtos.FollowResponse;
import com.coderberojgar.LinkedInClone.dto.SocialDtos.NotificationResponse;
import com.coderberojgar.LinkedInClone.entity.Follow;
import com.coderberojgar.LinkedInClone.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SocialMapper {

    @Mapping(target = "followerUserId", source = "follower.userId")
    @Mapping(target = "followerName", source = "follower.name")
    @Mapping(target = "followingUserId", source = "following.userId")
    @Mapping(target = "followingName", source = "following.name")
    FollowResponse toFollowResponse(Follow follow);

    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "actorUserId", source = "actor.userId")
    @Mapping(target = "actorName", source = "actor.name")
    NotificationResponse toNotificationResponse(Notification notification);
}
