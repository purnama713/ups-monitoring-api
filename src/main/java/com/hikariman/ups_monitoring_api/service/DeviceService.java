package com.hikariman.ups_monitoring_api.service;

import com.hikariman.ups_monitoring_api.entity.Device;
import com.hikariman.ups_monitoring_api.entity.User;
import com.hikariman.ups_monitoring_api.model.CreateDeviceRequest;
import com.hikariman.ups_monitoring_api.model.DeviceResponse;
import com.hikariman.ups_monitoring_api.model.UpdateDeviceRequest;
import com.hikariman.ups_monitoring_api.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private ValidationService validationService;

    private DeviceResponse toDeviceResponse(Device device) {
        return DeviceResponse
                .builder()
                .id(device.getId())
                .code(device.getCode())
                .name(device.getName())
                .location(device.getLocation())
                .batteryCount(device.getBatteryCount())
                .build();
    }

    @Transactional(readOnly = true)
    public List<DeviceResponse> getAllDevices(User user) {
        List<Device> devices = deviceRepository.findAllByUser(user);

//        if (devices.isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
//        }

        return devices.stream().map(this::toDeviceResponse).toList();
    }

    @Transactional(readOnly = true)
    public DeviceResponse get(User user, Integer id) {
        Device device = deviceRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found"));

        return toDeviceResponse(device);
    }

    @Transactional
    public DeviceResponse create(User user, CreateDeviceRequest request) {
        validationService.validate(request);

        Device device = new Device();
        device.setCode(request.getCode());
        device.setName(request.getName());
        device.setLocation(request.getLocation());
        device.setBatteryCount(request.getBatteryCount());
        device.setUser(user);
        deviceRepository.save(device);

        return toDeviceResponse(device);
    }

    @Transactional
    public DeviceResponse update(User user, UpdateDeviceRequest request) {
        validationService.validate(request);

        Device device = deviceRepository.findFirstByUserAndId(user, request.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found"));

        device.setName(request.getName());
        device.setLocation(request.getLocation());
        device.setBatteryCount(request.getBatteryCount());
        deviceRepository.save(device);

        return toDeviceResponse(device);
    }

    @Transactional
    public void delete(User user, Integer id) {
        Device device = deviceRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found"));

        deviceRepository.delete(device);
    }
}
