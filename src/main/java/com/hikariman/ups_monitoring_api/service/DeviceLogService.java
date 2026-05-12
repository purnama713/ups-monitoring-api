package com.hikariman.ups_monitoring_api.service;

import com.hikariman.ups_monitoring_api.entity.ApiKey;
import com.hikariman.ups_monitoring_api.entity.Device;
import com.hikariman.ups_monitoring_api.entity.DeviceLog;
import com.hikariman.ups_monitoring_api.entity.User;
import com.hikariman.ups_monitoring_api.model.CreateDeviceLogRequest;
import com.hikariman.ups_monitoring_api.model.DeviceLogResponse;
import com.hikariman.ups_monitoring_api.repository.ApiKeyRepository;
import com.hikariman.ups_monitoring_api.repository.DeviceLogRepository;
import com.hikariman.ups_monitoring_api.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DeviceLogService {

    @Autowired
    DeviceLogRepository deviceLogRepository;

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    ApiKeyRepository apiKeyRepository;

    @Autowired
    ValidationService validationService;

    private DeviceLogResponse toDeviceLogResponse(DeviceLog deviceLog) {
        return DeviceLogResponse
                .builder()
                .id(deviceLog.getId())
                .batteryVoltage(deviceLog.getBatteryVoltage())
                .inputVoltage(deviceLog.getInputVoltage())
                .chargingState(deviceLog.getChargingState())
                .recordedAt(deviceLog.getRecordedAt())
                .build();
    }

    @Transactional
    public DeviceLogResponse create(ApiKey apiKey, CreateDeviceLogRequest request) {
        validationService.validate(request);



        Device device = deviceRepository.findFirstByApiKeyAndCode(apiKey, request.getDeviceCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found"));

        DeviceLog deviceLog = new DeviceLog();
        deviceLog.setBatteryVoltage(request.getBatteryVoltage());
        deviceLog.setInputVoltage(request.getInputVoltage());
        deviceLog.setChargingState(request.getChargingState());
        deviceLog.setRecordedAt(request.getRecordedAt());
        deviceLog.setDevice(device);

        deviceLogRepository.save(deviceLog);

        return toDeviceLogResponse(deviceLog);
    }

    @Transactional(readOnly = true)
    public DeviceLogResponse get(User user, String deviceCode, Long id) {
        Device device = deviceRepository.findFirstByUserAndCode(user, deviceCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found"));

        DeviceLog deviceLog = deviceLogRepository.findFirstByDeviceAndId(device, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Log not found"));

        return toDeviceLogResponse(deviceLog);
    }

    @Transactional
    public Page<DeviceLogResponse> getAllByDevice(User user, String deviceCode) {
        Device device = deviceRepository.findFirstByUserAndCode(user, deviceCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found"));

        Pageable pageable = PageRequest.of(0, 50);
        Page<DeviceLog> deviceLog = deviceLogRepository.findAllByDevice(device, pageable);
        List<DeviceLogResponse> deviceLogResponses = deviceLog
                .getContent()
                .stream()
                .map(this::toDeviceLogResponse)
                .toList();

        return new PageImpl<>(deviceLogResponses, pageable, deviceLog.getTotalElements());
    }

    @Transactional
    public void delete(User user, Integer deviceId, Long addressId) {
        Device device = deviceRepository.findFirstByUserAndId(user, deviceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found"));

        DeviceLog deviceLog = deviceLogRepository.findFirstByDeviceAndId(device, addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Log not found"));

        deviceLogRepository.delete(deviceLog);
    }
}
