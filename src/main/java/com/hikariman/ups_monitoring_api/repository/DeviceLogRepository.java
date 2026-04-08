package com.hikariman.ups_monitoring_api.repository;

import com.hikariman.ups_monitoring_api.entity.Device;
import com.hikariman.ups_monitoring_api.entity.DeviceLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceLogRepository extends JpaRepository<DeviceLog, Long> {

    Page<DeviceLog> findAllByDevice(Device device, Pageable pageable);

    Optional<DeviceLog> findFirstByDeviceAndId(Device device, Long id);

//    Page<DeviceLog> findAllByMonth(LocalDateTime date, Pageable pageable);
}
