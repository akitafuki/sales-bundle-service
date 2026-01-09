package com.salesbundle.scheduler;

import com.salesbundle.model.Bundle;
import com.salesbundle.service.BundleIngestionService;
import com.salesbundle.service.DiscordNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class BundleScheduler {

    private final BundleIngestionService bundleIngestionService;
    private final DiscordNotificationService discordNotificationService;

    public BundleScheduler(BundleIngestionService bundleIngestionService, DiscordNotificationService discordNotificationService) {
        this.bundleIngestionService = bundleIngestionService;
        this.discordNotificationService = discordNotificationService;
    }

    // Run every day (86400000 ms)
    @Scheduled(fixedRate = 86400000)
    public void processBundles() {
        log.info("Scheduled task: Starting bundle processing...");
        try {
            List<Bundle> newBundles = bundleIngestionService.ingestLatestBundleData();
            if (!newBundles.isEmpty()) {
                log.info("Scheduled task: Ingested {} new bundles. Sending notifications...", newBundles.size());
                discordNotificationService.sendNotifications(newBundles);
            } else {
                log.info("Scheduled task: No new bundles found.");
            }
        } catch (Exception e) {
            log.error("Scheduled task: Error processing bundles", e);
        }
    }
}
