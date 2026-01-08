package com.salesbundle.controller;

import com.salesbundle.model.Bundle;
import com.salesbundle.model.Subscription;
import com.salesbundle.repository.SubscriptionRepository;
import com.salesbundle.service.BundleIngestionService;
import com.salesbundle.service.DiscordNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class StatusController {

    private final BundleIngestionService bundleIngestionService;
    private final DiscordNotificationService discordNotificationService;
    private final SubscriptionRepository subscriptionRepository;

    public StatusController(BundleIngestionService bundleIngestionService, DiscordNotificationService discordNotificationService, SubscriptionRepository subscriptionRepository) {
        this.bundleIngestionService = bundleIngestionService;
        this.discordNotificationService = discordNotificationService;
        this.subscriptionRepository = subscriptionRepository;
    }

    @PostMapping("/process")
    public ResponseEntity<String> process() {
        log.info("Starting processing cycle...");
        List<Bundle> newBundles = bundleIngestionService.ingestLatestBundleData();
        log.info("Ingested {} new bundles.", newBundles.size());
        
        discordNotificationService.sendNotifications(newBundles);
        
        return ResponseEntity.ok("Processed " + newBundles.size() + " new bundles.");
    }

    @PostMapping("/subscribe/{partnerId}")
    public ResponseEntity<String> subscribe(@PathVariable String partnerId) {
        if (subscriptionRepository.findByPartnerId(partnerId).isPresent()) {
            return ResponseEntity.badRequest().body("Partner already subscribed.");
        }
        subscriptionRepository.save(new Subscription(partnerId));
        return ResponseEntity.ok("Subscribed partner: " + partnerId);
    }
    
    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("Service is running.");
    }
}