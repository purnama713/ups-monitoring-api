package com.hikariman.ups_monitoring_api.service;

import com.hikariman.ups_monitoring_api.entity.ApiKey;
import com.hikariman.ups_monitoring_api.entity.Device;
import com.hikariman.ups_monitoring_api.entity.User;
import com.hikariman.ups_monitoring_api.model.ApiKeyResponse;
import com.hikariman.ups_monitoring_api.repository.ApiKeyRepository;
import com.hikariman.ups_monitoring_api.repository.DeviceRepository;
import com.hikariman.ups_monitoring_api.security.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class ApiKeyService {

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private ValidationService validationService;

    private ApiKeyResponse toApiKeyResponse(ApiKey apiKey, String key) {
        return ApiKeyResponse
                .builder()
                .id(apiKey.getId())
                .deviceCode(apiKey.getDevice().getCode())
                .apiKey(key)
                .build();
    }

    @Transactional
    public ApiKeyResponse create(User user, String deviceCode) {

        Device device = deviceRepository.findFirstByUserAndCode(user, deviceCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found"));

        String prefix = "iot_live_";
        String randomUuid = UUID.randomUUID().toString();
        String key = prefix + randomUuid;

        ApiKey apiKey = new ApiKey();
        apiKey.setDevice(device);
        apiKey.setApiKey(BCrypt.hashpw(key, BCrypt.gensalt()));
        apiKeyRepository.save(apiKey);

        return toApiKeyResponse(apiKey, key);
    }

    @Transactional
    public ApiKeyResponse update(String deviceCode) {

        ApiKey apiKey = apiKeyRepository.findFirstByDeviceCode(deviceCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Api Key not found"));

        String prefix = "iot_live_";
        String randomUuid = UUID.randomUUID().toString();
        String key = prefix + randomUuid;

        apiKey.setApiKey(BCrypt.hashpw(key, BCrypt.gensalt()));
        apiKeyRepository.save(apiKey);

        return toApiKeyResponse(apiKey, key);
    }
}
