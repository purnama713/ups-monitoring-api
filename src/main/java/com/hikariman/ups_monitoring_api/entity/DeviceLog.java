package com.hikariman.ups_monitoring_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "device_logs")
@EntityListeners(AuditingEntityListener.class)
public class DeviceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "battery_voltage", columnDefinition = "json")
    private Map<String, Double> batteryVoltage;

    @Column(name = "input_voltage")
    private Double inputVoltage;

    @Column(name = "charging_state")
    private String chargingState;

    @CreatedDate
    @Column(name = "recorded_at")
    private Instant recordedAt;

    @ManyToOne
    @JoinColumn(name = "device_code", referencedColumnName = "code")
    private Device device;
}
