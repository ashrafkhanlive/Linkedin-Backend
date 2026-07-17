package com.coderberojgar.LinkedInClone.service;

import com.coderberojgar.LinkedInClone.dto.GroupDtos.GroupRequest;
import com.coderberojgar.LinkedInClone.dto.GroupDtos.GroupResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GroupService {

    GroupResponse create(GroupRequest request);

    GroupResponse join(Long groupId);

    void leave(Long groupId);

    void delete(Long groupId);

    Page<GroupResponse> search(String query, Pageable pageable);
}
