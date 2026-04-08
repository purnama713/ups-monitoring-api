package com.hikariman.ups_monitoring_api.model;

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
public class DeviceLogResponse {

    public Long id;

    public Integer deviceId;

    public Map<String, Double> batteryVoltage;

    public Double inputVoltage;

    public String chargingState;

    public Instant recordedAt;
}
