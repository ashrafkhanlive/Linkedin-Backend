package com.coderberojgar.LinkedInClone.service.impl;

import com.coderberojgar.LinkedInClone.constant.ConnectionStatus;
import com.coderberojgar.LinkedInClone.dto.ConnectionDtos.ConnectionResponse;
import com.coderberojgar.LinkedInClone.entity.Connection;
import com.coderberojgar.LinkedInClone.entity.User;
import com.coderberojgar.LinkedInClone.exception.BadRequestException;
import com.coderberojgar.LinkedInClone.exception.ResourceNotFoundException;
import com.coderberojgar.LinkedInClone.mapper.ConnectionMapper;
import com.coderberojgar.LinkedInClone.repository.ConnectionRepository;
import com.coderberojgar.LinkedInClone.service.ConnectionService;
import com.coderberojgar.LinkedInClone.service.UserService;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ConnectionServiceImpl implements ConnectionService {

    private final ConnectionRepository connectionRepository;
    private final UserService userService;
    private final ConnectionMapper mapper;

    public ConnectionServiceImpl(ConnectionRepository connectionRepository, UserService userService, ConnectionMapper mapper) {
        this.connectionRepository = connectionRepository;
        this.userService = userService;
        this.mapper = mapper;
    }

    @Override
    public ConnectionResponse sendRequest(Long userId) {
        User current = userService.currentUser();
        if (current.getUserId().equals(userId)) {
            throw new BadRequestException("Cannot connect with yourself");
        }
        User target = userService.getUser(userId);
        if (connectionRepository.findByUserUserIdAndConnectionUserUserId(current.getUserId(), userId).isPresent()) {
            throw new BadRequestException("Connection request already exists");
        }
        Connection connection = new Connection();
        connection.setUser(current);
        connection.setConnectionUser(target);
        connection.setConnectionStatus(ConnectionStatus.PENDING);
        connection.setRequestedAt(Instant.now());
        return mapper.toResponse(connectionRepository.save(connection));
    }

    @Override
    public ConnectionResponse accept(Long connectionId) {
        Connection request = pendingForCurrentUser(connectionId);
        request.setConnectionStatus(ConnectionStatus.ACCEPTED);
        connectionRepository.findByUserUserIdAndConnectionUserUserId(request.getConnectionUser().getUserId(), request.getUser().getUserId())
                .orElseGet(() -> {
                    Connection reciprocal = new Connection();
                    reciprocal.setUser(request.getConnectionUser());
                    reciprocal.setConnectionUser(request.getUser());
                    reciprocal.setConnectionStatus(ConnectionStatus.ACCEPTED);
                    reciprocal.setRequestedAt(Instant.now());
                    return connectionRepository.save(reciprocal);
                });
        return mapper.toResponse(request);
    }

    @Override
    public ConnectionResponse reject(Long connectionId) {
        Connection request = pendingForCurrentUser(connectionId);
        request.setConnectionStatus(ConnectionStatus.REJECTED);
        return mapper.toResponse(request);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ConnectionResponse> requests(Pageable pageable) {
        return connectionRepository.findByConnectionUserUserIdAndConnectionStatusAndDeletedFalse(
                userService.currentUser().getUserId(), ConnectionStatus.PENDING, pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ConnectionResponse> connections(Pageable pageable) {
        return connectionRepository.findByUserUserIdAndConnectionStatusAndDeletedFalse(
                userService.currentUser().getUserId(), ConnectionStatus.ACCEPTED, pageable).map(mapper::toResponse);
    }

    private Connection pendingForCurrentUser(Long connectionId) {
        Connection request = connectionRepository.findById(connectionId)
                .filter(connection -> !connection.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Connection request not found"));
        if (!request.getConnectionUser().getUserId().equals(userService.currentUser().getUserId())) {
            throw new BadRequestException("This request is not assigned to you");
        }
        if (request.getConnectionStatus() != ConnectionStatus.PENDING) {
            throw new BadRequestException("Connection request is not pending");
        }
        return request;
    }
}
