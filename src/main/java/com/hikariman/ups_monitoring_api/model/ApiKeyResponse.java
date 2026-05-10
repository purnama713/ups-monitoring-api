package com.hikariman.ups_monitoring_api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiKeyResponse {

    private Integer id;

    private String deviceCode;

    private String apiKey;

    private Instant createdAt;
}
