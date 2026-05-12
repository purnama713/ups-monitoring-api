package com.hikariman.ups_monitoring_api.repository;

import com.hikariman.ups_monitoring_api.entity.ApiKey;
import com.hikariman.ups_monitoring_api.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Integer> {

    Optional<ApiKey> findFirstByApiKey(String apiKey);

    Optional<ApiKey> findFirstByDevice(Device device);

    Optional<ApiKey> findFirstByDeviceCode(String deviceCode);
}
