package com.hikariman.ups_monitoring_api.controller;

import com.hikariman.ups_monitoring_api.entity.ApiKey;
import com.hikariman.ups_monitoring_api.entity.Device;
import com.hikariman.ups_monitoring_api.entity.DeviceLog;
import com.hikariman.ups_monitoring_api.entity.User;
import com.hikariman.ups_monitoring_api.model.CreateDeviceLogRequest;
import com.hikariman.ups_monitoring_api.model.DeviceLogResponse;
import com.hikariman.ups_monitoring_api.model.WebResponse;
import com.hikariman.ups_monitoring_api.repository.ApiKeyRepository;
import com.hikariman.ups_monitoring_api.repository.DeviceLogRepository;
import com.hikariman.ups_monitoring_api.repository.DeviceRepository;
import com.hikariman.ups_monitoring_api.repository.UserRepository;
import com.hikariman.ups_monitoring_api.security.BCrypt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DeviceLogControllerTest {

    @Autowired
    private DeviceLogRepository deviceLogRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        deviceLogRepository.deleteAll();
        apiKeyRepository.deleteAll();
        deviceRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("usertest");
        user.setPassword(BCrypt.hashpw("test123", BCrypt.gensalt()));
        user.setName("hikariman");
        user.setToken("testtoken");
        user.setTokenExpiredAt(System.currentTimeMillis() + 1000000000000L);
        userRepository.save(user);

        Device device = new Device();
        device.setUser(user);
        device.setCode("UPS-261220-ABCD");
        device.setName("APC 600va");
        device.setBatteryCount(2);
        device.setLocation("Bedroom");
        deviceRepository.save(device);

        ApiKey apiKey = new ApiKey();
        apiKey.setDevice(device);
        apiKey.setApiKey(BCrypt.hashpw("testapikey", BCrypt.gensalt()));
        apiKeyRepository.save(apiKey);
    }

    @Test
    void create() throws Exception {
        CreateDeviceLogRequest request = new CreateDeviceLogRequest();
        request.setChargingState("Discharging");
        request.setBatteryVoltage(Map.of(
                "cell_1", 14.2,
                "cell_2", 14.4));
        request.setInputVoltage(220.0);

        mockMvc.perform(
                post("/api/devices/UPS-261220-ABCD/logs")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-KEY", "testapikey")
                        .header("X-DEVICE-CODE", "UPS-261220-ABCD")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<DeviceLogResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});

            assertNull(response.getErrors());
        });
    }

    @Test
    void getAllByDevice() throws Exception {
        Device device = deviceRepository.findByCode("UPS-261220-ABCD").orElseThrow();

        for (double i = 0; i < 10; i++) {
            DeviceLog deviceLog = new DeviceLog();
            deviceLog.setDevice(device);
            deviceLog.setChargingState("Charging");
            deviceLog.setBatteryVoltage(Map.of(
                    "cell_1", 14.2+(i/10),
                    "cell_2", 14.4+(i/10)));
            deviceLog.setInputVoltage(220.0+(i/10));
            deviceLogRepository.save(deviceLog);
        }

        mockMvc.perform(
                get("/api/devices/UPS-261220-ABCD/logs")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testtoken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<DeviceLogResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});

            assertNull(response.getErrors());
        });
    }

}