package com.hikariman.ups_monitoring_api.controller;

import com.hikariman.ups_monitoring_api.entity.Device;
import com.hikariman.ups_monitoring_api.entity.DeviceLog;
import com.hikariman.ups_monitoring_api.model.CreateDeviceLogRequest;
import com.hikariman.ups_monitoring_api.model.DeviceLogResponse;
import com.hikariman.ups_monitoring_api.model.WebResponse;
import com.hikariman.ups_monitoring_api.repository.DeviceLogRepository;
import com.hikariman.ups_monitoring_api.repository.DeviceRepository;
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
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        deviceLogRepository.deleteAll();
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
                post("/api/devices/233/logs")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "testtoken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<DeviceLogResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});

            assertNull(response.getErrors());
        });
    }

    @Test
    void getAllByDevice() throws Exception {
        Device device = deviceRepository.findById(233).orElseThrow();

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
                get("/api/devices/233/logs")
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

    @Test
    void delete() {
    }
}