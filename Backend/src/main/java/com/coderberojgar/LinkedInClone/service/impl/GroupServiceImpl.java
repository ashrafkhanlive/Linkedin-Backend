package com.coderberojgar.LinkedInClone.service.impl;

import com.coderberojgar.LinkedInClone.dto.GroupDtos.GroupRequest;
import com.coderberojgar.LinkedInClone.dto.GroupDtos.GroupResponse;
import com.coderberojgar.LinkedInClone.entity.GroupMember;
import com.coderberojgar.LinkedInClone.entity.LinkedInGroup;
import com.coderberojgar.LinkedInClone.entity.User;
import com.coderberojgar.LinkedInClone.exception.BadRequestException;
import com.coderberojgar.LinkedInClone.exception.ResourceNotFoundException;
import com.coderberojgar.LinkedInClone.mapper.GroupMapper;
import com.coderberojgar.LinkedInClone.repository.GroupMemberRepository;
import com.coderberojgar.LinkedInClone.repository.GroupRepository;
import com.coderberojgar.LinkedInClone.service.GroupService;
import com.coderberojgar.LinkedInClone.service.UserService;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserService userService;
    private final GroupMapper mapper;

    public GroupServiceImpl(GroupRepository groupRepository,
                            GroupMemberRepository groupMemberRepository,
                            UserService userService,
                            GroupMapper mapper) {
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userService = userService;
        this.mapper = mapper;
    }

    @Override
    public GroupResponse create(GroupRequest request) {
        User owner = userService.currentUser();
        LinkedInGroup group = new LinkedInGroup();
        group.setUser(owner);
        group.setGroupName(request.groupName());
        group.setDescription(request.description());
        group.setCreatedDate(Instant.now());
        LinkedInGroup saved = groupRepository.save(group);
        addMember(saved, owner);
        return mapper.toResponse(saved);
    }

    @Override
    public GroupResponse join(Long groupId) {
        LinkedInGroup group = getGroup(groupId);
        User user = userService.currentUser();
        groupMemberRepository.findByGroupGroupIdAndUserUserId(groupId, user.getUserId())
                .orElseGet(() -> addMember(group, user));
        return mapper.toResponse(group);
    }

    @Override
    public void leave(Long groupId) {
        User user = userService.currentUser();
        GroupMember member = groupMemberRepository.findByGroupGroupIdAndUserUserId(groupId, user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Group membership not found"));
        if (member.getGroup().getUser().getUserId().equals(user.getUserId())) {
            throw new BadRequestException("Group owner cannot leave without deleting the group");
        }
        groupMemberRepository.delete(member);
    }

    @Override
    public void delete(Long groupId) {
        LinkedInGroup group = getGroup(groupId);
        if (!group.getUser().getUserId().equals(userService.currentUser().getUserId())) {
            throw new BadRequestException("Only the group owner can delete this group");
        }
        group.setDeleted(true);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GroupResponse> search(String query, Pageable pageable) {
        return groupRepository.findByGroupNameContainingIgnoreCaseAndDeletedFalse(query == null ? "" : query, pageable)
                .map(mapper::toResponse);
    }

    private LinkedInGroup getGroup(Long groupId) {
        return groupRepository.findById(groupId)
                .filter(group -> !group.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
    }

    private GroupMember addMember(LinkedInGroup group, User user) {
        GroupMember member = new GroupMember();
        member.setGroup(group);
        member.setUser(user);
        member.setJoinedAt(Instant.now());
        return groupMemberRepository.save(member);
    }
}
