package com.hikariman.ups_monitoring_api.repository;

import com.hikariman.ups_monitoring_api.entity.ApiKey;
import com.hikariman.ups_monitoring_api.entity.Device;
import com.hikariman.ups_monitoring_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device,Integer> {

    Optional<Device> findFirstByUserAndId(User user, Integer id);

    Optional<Device> findFirstByUserAndCode(User user, String code);

    Optional<Device> findByCode(String code);

    Optional<Device> findFirstByApiKeyAndCode(ApiKey apiKey, String code);

    List<Device> findAllByUser(User user);
}