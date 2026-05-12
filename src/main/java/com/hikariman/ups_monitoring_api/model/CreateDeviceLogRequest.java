package com.hikariman.ups_monitoring_api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateDeviceLogRequest {

    @JsonIgnore
    @NotBlank
    private String deviceCode;

    private Map<String, Double> batteryVoltage;

    private Double inputVoltage;

    @Size(max = 20)
    private String chargingState;

    private Instant recordedAt;
}
