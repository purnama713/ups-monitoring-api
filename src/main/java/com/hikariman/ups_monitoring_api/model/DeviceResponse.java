package com.hikariman.ups_monitoring_api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceResponse {

    private Integer id;

    private String code;

    private String name;

    private String location;

    private Integer batteryCount;

    private String registeredAt;
}
