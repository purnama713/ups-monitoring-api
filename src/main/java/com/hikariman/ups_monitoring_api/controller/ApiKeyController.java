package com.hikariman.ups_monitoring_api.controller;

import com.hikariman.ups_monitoring_api.entity.User;
import com.hikariman.ups_monitoring_api.model.ApiKeyResponse;
import com.hikariman.ups_monitoring_api.model.WebResponse;
import com.hikariman.ups_monitoring_api.service.ApiKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiKeyController {

    @Autowired
    private ApiKeyService apiKeyService;

    @PostMapping(
            path = "/api/devices/{deviceCode}/api-keys",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ApiKeyResponse> create(User user, @PathVariable String deviceCode) {
        ApiKeyResponse apiKeyResponse = apiKeyService.create(user, deviceCode);
        return WebResponse.<ApiKeyResponse>builder().data(apiKeyResponse).build();
    }

    @PutMapping(
            path = "/api/devices/{deviceCode}/api-keys",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ApiKeyResponse> update(@PathVariable String deviceCode) {
        ApiKeyResponse apiKeyResponse = apiKeyService.update(deviceCode);
        return WebResponse.<ApiKeyResponse>builder().data(apiKeyResponse).build();
    }
}
