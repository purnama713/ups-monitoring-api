package com.hikariman.ups_monitoring_api.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateDeviceRequest {

    @NotBlank
    @Size(max=50)
    private String name;

    @Size(max = 30)
    private String location;

    @NotNull
    @Min(0)
    private Integer batteryCount;
}
