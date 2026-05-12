package com.hikariman.ups_monitoring_api.controller;

import com.hikariman.ups_monitoring_api.entity.Device;
import com.hikariman.ups_monitoring_api.entity.User;
import com.hikariman.ups_monitoring_api.model.ApiKeyResponse;
import com.hikariman.ups_monitoring_api.model.DeviceResponse;
import com.hikariman.ups_monitoring_api.model.WebResponse;
import com.hikariman.ups_monitoring_api.repository.ApiKeyRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiKeyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @Test
    void create() throws Exception {
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

        mockMvc.perform(
                post("/api/devices/UPS-261220-ABCD/api-keys")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testtoken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<ApiKeyResponse> response = objectMapper.readValue(result
                    .getResponse()
                    .getContentAsString(), new TypeReference<>(){});

            assertNull(response.getErrors());
        });
    }

    @Test
    void update() throws Exception {
        mockMvc.perform(
                put("/api/devices/UPS-261220-ABCD/api-keys")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testtoken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<ApiKeyResponse> response = objectMapper.readValue(result
                    .getResponse()
                    .getContentAsString(), new TypeReference<>(){});

            assertNull(response.getErrors());
        });
    }

    @Test
    void delete() throws Exception {
        apiKeyRepository.deleteAll();
    }
}