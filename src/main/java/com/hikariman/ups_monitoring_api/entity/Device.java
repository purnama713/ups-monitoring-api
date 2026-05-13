package com.hikariman.ups_monitoring_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "devices")
@EntityListeners(AuditingEntityListener.class) // Untuk tanggal dibuat
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code", unique = true)
    private String code;

    private String name;
    private String location;

    @Column(name = "battery_count")
    private Integer batteryCount;

    @CreatedDate
    @Column(name = "registered_at")
    private Instant registeredAt;

    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User user;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
    private List<DeviceLog> deviceLogs;

    @OneToOne(mappedBy = "device", cascade = CascadeType.ALL)
    private ApiKey apiKey;
}
