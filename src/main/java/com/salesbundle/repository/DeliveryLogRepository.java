package com.salesbundle.repository;

import com.salesbundle.model.DeliveryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliveryLogRepository extends JpaRepository<DeliveryLog, Long> {
    Optional<DeliveryLog> findByBundleIdAndPartnerId(Long bundleId, String partnerId);
}