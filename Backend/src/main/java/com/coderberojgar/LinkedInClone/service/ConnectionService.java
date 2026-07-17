package com.coderberojgar.LinkedInClone.service;

import com.coderberojgar.LinkedInClone.dto.ConnectionDtos.ConnectionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ConnectionService {

    ConnectionResponse sendRequest(Long userId);

    ConnectionResponse accept(Long connectionId);

    ConnectionResponse reject(Long connectionId);

    Page<ConnectionResponse> requests(Pageable pageable);

    Page<ConnectionResponse> connections(Pageable pageable);
}
