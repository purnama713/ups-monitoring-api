package com.hikariman.ups_monitoring_api.controller;

import com.hikariman.ups_monitoring_api.entity.User;
import com.hikariman.ups_monitoring_api.model.CreateDeviceRequest;
import com.hikariman.ups_monitoring_api.model.DeviceResponse;
import com.hikariman.ups_monitoring_api.model.UpdateDeviceRequest;
import com.hikariman.ups_monitoring_api.model.WebResponse;
import com.hikariman.ups_monitoring_api.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @PostMapping(
            path = "/api/devices",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<DeviceResponse> create(User user, @RequestBody CreateDeviceRequest request) {
        DeviceResponse deviceResponse = deviceService.create(user, request);
        return WebResponse.<DeviceResponse>builder().data(deviceResponse).build();
    }

    @GetMapping(
            path = "/api/devices/{deviceId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<DeviceResponse> get(User user, @PathVariable("deviceId") Integer deviceId) {
        DeviceResponse deviceResponse = deviceService.get(user, deviceId);
        return WebResponse.<DeviceResponse>builder().data(deviceResponse).build();
    }

    @PutMapping(
            path = "/api/devices/{deviceId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<DeviceResponse> update(User user, @RequestBody UpdateDeviceRequest request,  @PathVariable("deviceId") Integer deviceId) {
        request.setId(deviceId);

        DeviceResponse deviceResponse = deviceService.update(user, request);
        return WebResponse.<DeviceResponse>builder().data(deviceResponse).build();
    }

    @DeleteMapping(
            path = "/api/devices/{deviceId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> delete(User user, @PathVariable("deviceId") Integer deviceId) {
        deviceService.delete(user, deviceId);
        return WebResponse.<String>builder().data("OK").build();
    }

    @GetMapping(
            path = "/api/users/current/devices",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<DeviceResponse>> getAll(User user) {
        List<DeviceResponse> deviceResponses = deviceService.getAllDevices(user);
        return WebResponse.<List<DeviceResponse>>builder().data(deviceResponses).build();
    }
}
