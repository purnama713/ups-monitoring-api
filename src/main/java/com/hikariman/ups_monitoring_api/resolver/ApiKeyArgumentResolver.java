package com.hikariman.ups_monitoring_api.resolver;

import com.hikariman.ups_monitoring_api.entity.ApiKey;
import com.hikariman.ups_monitoring_api.entity.Device;
import com.hikariman.ups_monitoring_api.entity.User;
import com.hikariman.ups_monitoring_api.repository.ApiKeyRepository;
import com.hikariman.ups_monitoring_api.repository.DeviceRepository;
import com.hikariman.ups_monitoring_api.security.BCrypt;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ApiKeyArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return ApiKey.class.equals(parameter.getParameterType());
    }

    @Override
    public @Nullable Object resolveArgument(MethodParameter parameter,
                                            @Nullable ModelAndViewContainer mavContainer,
                                            NativeWebRequest webRequest,
                                            @Nullable WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest servletRequest = (HttpServletRequest) webRequest.getNativeRequest();
        String key = servletRequest.getHeader("X-API-KEY");
        String deviceCode = servletRequest.getHeader("X-DEVICE-CODE");

        if (key == null || deviceCode == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        ApiKey apiKey = apiKeyRepository.findFirstByDeviceCode(deviceCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        if (!BCrypt.checkpw(key, apiKey.getApiKey())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        return apiKey;
    }
}
