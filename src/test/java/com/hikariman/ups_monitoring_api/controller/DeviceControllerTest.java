package com.hikariman.ups_monitoring_api.controller;

import com.hikariman.ups_monitoring_api.entity.Device;
import com.hikariman.ups_monitoring_api.entity.User;
import com.hikariman.ups_monitoring_api.model.*;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        deviceRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("usertest");
        user.setPassword(BCrypt.hashpw("test123", BCrypt.gensalt()));
        user.setName("hikariman");
        user.setToken("testtoken");
        user.setTokenExpiredAt(System.currentTimeMillis() + 1000000000000L);
        userRepository.save(user);
    }

    @Test
    void createSuccess() throws Exception {
        CreateDeviceRequest request = new CreateDeviceRequest();
        request.setCode("test-1");
        request.setName("APC");
        request.setLocation("bedroom");
        request.setBatteryCount(2);

        mockMvc.perform(
                post("/api/devices")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "testtoken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<DeviceResponse> response = objectMapper.readValue(result
                    .getResponse()
                    .getContentAsString(), new TypeReference<>(){});

            assertNull(response.getErrors());
            assertEquals("APC", response.getData().getName());
            assertEquals(2, response.getData().getBatteryCount());
            assertEquals("test-1", response.getData().getCode());
            assertEquals("bedroom", response.getData().getLocation());
        });
    }

    @Test
    void createBadRequest() throws Exception {
        CreateDeviceRequest request = new CreateDeviceRequest();
        request.setCode("test-1");
        request.setName("test");
        request.setLocation("bedroom");
        request.setBatteryCount(-1);

        mockMvc.perform(
                post("/api/devices")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<DeviceResponse> response = objectMapper.readValue(result
                    .getResponse()
                    .getContentAsString(), new TypeReference<>(){});

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getSuccess() throws Exception {
        User user = userRepository.findById("test").orElseThrow();

        Device device = new Device();
        device.setUser(user);
        device.setCode("test-1");
        device.setName("test");
        device.setLocation("bedroom");
        device.setBatteryCount(2);
        deviceRepository.save(device);

        mockMvc.perform(
                get("/api/devices/" + device.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<DeviceResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});

            assertNull(response.getErrors());
            assertEquals(device.getId(), response.getData().getId());
            assertEquals(device.getCode(), response.getData().getCode());
            assertEquals(device.getName(), response.getData().getName());
            assertEquals(device.getLocation(), response.getData().getLocation());
            assertEquals(device.getBatteryCount(), response.getData().getBatteryCount());
        });
    }

    @Test
    void getUnauthorized() throws Exception {
        User user = userRepository.findById("test").orElseThrow();

        Device device = new Device();
        device.setUser(user);
        device.setCode("test-1");
        device.setName("test");
        device.setLocation("bedroom");
        device.setBatteryCount(2);
        deviceRepository.save(device);

        mockMvc.perform(
                get("/api/devices/" + device.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "t")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getNotFound() throws Exception {
        User user = userRepository.findById("test").orElseThrow();

        Device device = new Device();
        device.setUser(user);
        device.setCode("test-1");
        device.setName("test");
        device.setLocation("bedroom");
        device.setBatteryCount(2);
        deviceRepository.save(device);

        mockMvc.perform(
                get("/api/devices/" + 123)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void updateSuccess() throws Exception {
        User user = userRepository.findById("test").orElseThrow();

        Device device = new Device();
        device.setUser(user);
        device.setCode("test-1");
        device.setName("test");
        device.setLocation("bedroom");
        device.setBatteryCount(2);
        deviceRepository.save(device);

        UpdateDeviceRequest request = new UpdateDeviceRequest();
        request.setName("Inforce");
        request.setLocation("Gudang");
        request.setBatteryCount(3);

        mockMvc.perform(
                put("/api/devices/" + device.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<DeviceResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});

            assertNull(response.getErrors());
            assertEquals(request.getName(), response.getData().getName());
            assertEquals(request.getLocation(), response.getData().getLocation());
            assertEquals(request.getBatteryCount(), response.getData().getBatteryCount());

            assertTrue(deviceRepository.existsById(response.getData().getId()));
        });
    }

    @Test
    void updateBadRequest() throws Exception {
        UpdateDeviceRequest request = new UpdateDeviceRequest();
        request.setName("Inforce");
        request.setLocation("Gudang");

        mockMvc.perform(
                put("/api/devices/129")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper
                    .readValue(result
                            .getResponse()
                            .getContentAsString(), new TypeReference<WebResponse<String>>(){});

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void updateUnauthorized() throws Exception {
        User user = userRepository.findById("test").orElseThrow();

        Device device = new Device();
        device.setUser(user);
        device.setCode("test-1");
        device.setName("test");
        device.setLocation("bedroom");
        device.setBatteryCount(2);
        deviceRepository.save(device);

        UpdateDeviceRequest request = new UpdateDeviceRequest();
        request.setName("Inforce");
        request.setLocation("Gudang");
        request.setBatteryCount(3);

        mockMvc.perform(
                put("/api/devices/" + device.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "testsalah")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void deleteSuccess() throws Exception {
        User user = userRepository.findById("test").orElseThrow();

        Device device = new Device();
        device.setUser(user);
        device.setCode("test-1");
        device.setName("test");
        device.setLocation("bedroom");
        device.setBatteryCount(2);
        deviceRepository.save(device);

        mockMvc.perform(
                delete("/api/devices/" + device.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testtoken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});

            assertNull(response.getErrors());
            assertEquals("OK", response.getData());
        });
    }

    @Test
    void deleteUnauthorized() throws Exception {
        User user = userRepository.findById("usertest").orElseThrow();

        Device device = new Device();
        device.setUser(user);
        device.setCode("test-1");
        device.setName("test");
        device.setLocation("bedroom");
        device.setBatteryCount(2);
        deviceRepository.save(device);

        mockMvc.perform(
                delete("/api/devices/" + device.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testsalah")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void deleteNotFound() throws Exception {
        mockMvc.perform(
                delete("/api/devices/123")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testtoken")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){});

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getAllSuccess() throws Exception {
        User user = userRepository.findById("usertest").orElseThrow();

        for (int i = 0; i < 10; i++) {
            Device device = new Device();
            device.setUser(user);
            device.setCode("test-" + i);
            device.setName("test");
            device.setLocation("bedroom");
            device.setBatteryCount(2);
            deviceRepository.save(device);
        }

        mockMvc.perform(
                get("/api/users/current/devices")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testtoken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<DeviceResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});

            assertNull(response.getErrors());
            assertEquals(10, response.getData().size());
            System.out.println(response.getData());
        });
    }

    @Test
    void getAllUnauthorized() throws Exception {
        mockMvc.perform(
                get("/api/users/current/devices")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "salah")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});

            assertNotNull(response.getErrors());
//            System.out.println(response.getData());
        });
    }
}