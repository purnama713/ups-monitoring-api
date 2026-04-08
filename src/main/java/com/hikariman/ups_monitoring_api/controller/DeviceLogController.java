package com.hikariman.ups_monitoring_api.controller;

import com.hikariman.ups_monitoring_api.entity.User;
import com.hikariman.ups_monitoring_api.model.CreateDeviceLogRequest;
import com.hikariman.ups_monitoring_api.model.DeviceLogResponse;
import com.hikariman.ups_monitoring_api.model.PagingResponse;
import com.hikariman.ups_monitoring_api.model.WebResponse;
import com.hikariman.ups_monitoring_api.service.DeviceLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DeviceLogController {

    @Autowired
    DeviceLogService deviceLogService;

    @PostMapping(
            path = "/api/devices/{deviceId}/logs",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<DeviceLogResponse> create(
                                                 @RequestBody CreateDeviceLogRequest request,
                                                 @PathVariable Integer deviceId) {
        request.setDeviceId(deviceId);
        DeviceLogResponse deviceLogResponse = deviceLogService.create(request);

        return WebResponse.<DeviceLogResponse>builder().data(deviceLogResponse).build();
    }

    @GetMapping(
            path = "/api/devices/{deviceId}/logs",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<DeviceLogResponse>> getAllByDevice(User user, @PathVariable Integer deviceId) {

        Page<DeviceLogResponse> deviceLogResponse = deviceLogService.getAllByDevice(user, deviceId);

        return WebResponse.<List<DeviceLogResponse>>builder()
                .data(deviceLogResponse.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(deviceLogResponse.getNumber())
                        .totalPages(deviceLogResponse.getTotalPages())
                        .size(deviceLogResponse.getSize())
                        .build())
                .build();
    }

    @DeleteMapping(
            path = "/api/device/{deviceId}/logs/{deviceLogId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> delete(User user,
                                                 @PathVariable Integer deviceId,
                                                 @PathVariable Long deviceLogId) {

        deviceLogService.delete(user, deviceId, deviceLogId);
        return WebResponse.<String>builder().data("OK").build();
    }
}
