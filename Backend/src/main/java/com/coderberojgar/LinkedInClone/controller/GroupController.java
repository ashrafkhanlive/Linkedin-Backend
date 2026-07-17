package com.coderberojgar.LinkedInClone.controller;

import com.coderberojgar.LinkedInClone.constant.ApiPaths;
import com.coderberojgar.LinkedInClone.dto.GroupDtos.GroupRequest;
import com.coderberojgar.LinkedInClone.dto.GroupDtos.GroupResponse;
import com.coderberojgar.LinkedInClone.response.ApiResponse;
import com.coderberojgar.LinkedInClone.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.GROUPS)
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GroupResponse>> create(@Valid @RequestBody GroupRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Group created", groupService.create(request)));
    }

    @PostMapping("/{groupId}/join")
    public ResponseEntity<ApiResponse<GroupResponse>> join(@PathVariable Long groupId) {
        return ResponseEntity.ok(ApiResponse.success("Joined group", groupService.join(groupId)));
    }

    @PostMapping("/{groupId}/leave")
    public ResponseEntity<ApiResponse<Void>> leave(@PathVariable Long groupId) {
        groupService.leave(groupId);
        return ResponseEntity.ok(ApiResponse.success("Left group"));
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long groupId) {
        groupService.delete(groupId);
        return ResponseEntity.ok(ApiResponse.success("Group deleted"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<GroupResponse>>> search(@RequestParam(defaultValue = "") String q, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Groups found", groupService.search(q, pageable)));
    }
}

