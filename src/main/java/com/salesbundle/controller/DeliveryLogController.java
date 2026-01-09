package com.salesbundle.controller;

import com.salesbundle.model.DeliveryLog;
import com.salesbundle.repository.DeliveryLogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/delivery-logs")
public class DeliveryLogController {

    private final DeliveryLogRepository deliveryLogRepository;

    public DeliveryLogController(DeliveryLogRepository deliveryLogRepository) {
        this.deliveryLogRepository = deliveryLogRepository;
    }

    @GetMapping
    public List<DeliveryLog> getAllDeliveryLogs() {
        return deliveryLogRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryLog> getDeliveryLogById(@PathVariable Long id) {
        return deliveryLogRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public DeliveryLog createDeliveryLog(@RequestBody DeliveryLog deliveryLog) {
        return deliveryLogRepository.save(deliveryLog);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeliveryLog> updateDeliveryLog(@PathVariable Long id, @RequestBody DeliveryLog deliveryLogDetails) {
        return deliveryLogRepository.findById(id)
                .map(deliveryLog -> {
                    deliveryLog.setBundleId(deliveryLogDetails.getBundleId());
                    deliveryLog.setPartnerId(deliveryLogDetails.getPartnerId());
                    return ResponseEntity.ok(deliveryLogRepository.save(deliveryLog));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeliveryLog(@PathVariable Long id) {
        if (deliveryLogRepository.existsById(id)) {
            deliveryLogRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
