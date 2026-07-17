package com.coderberojgar.LinkedInClone.controller;

import com.coderberojgar.LinkedInClone.constant.ApiPaths;
import com.coderberojgar.LinkedInClone.dto.ConnectionDtos.ConnectionResponse;
import com.coderberojgar.LinkedInClone.response.ApiResponse;
import com.coderberojgar.LinkedInClone.service.ConnectionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.CONNECTIONS)
public class ConnectionController {

    private final ConnectionService connectionService;

    public ConnectionController(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @PostMapping("/requests/{userId}")
    public ResponseEntity<ApiResponse<ConnectionResponse>> send(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Connection request sent", connectionService.sendRequest(userId)));
    }

    @PostMapping("/{connectionId}/accept")
    public ResponseEntity<ApiResponse<ConnectionResponse>> accept(@PathVariable Long connectionId) {
        return ResponseEntity.ok(ApiResponse.success("Connection accepted", connectionService.accept(connectionId)));
    }

    @PostMapping("/{connectionId}/reject")
    public ResponseEntity<ApiResponse<ConnectionResponse>> reject(@PathVariable Long connectionId) {
        return ResponseEntity.ok(ApiResponse.success("Connection rejected", connectionService.reject(connectionId)));
    }

    @GetMapping("/requests")
    public ResponseEntity<ApiResponse<Page<ConnectionResponse>>> requests(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Connection requests loaded", connectionService.requests(pageable)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ConnectionResponse>>> connections(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Connections loaded", connectionService.connections(pageable)));
    }
}

