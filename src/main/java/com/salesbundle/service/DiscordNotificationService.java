package com.salesbundle.service;

import com.salesbundle.model.Bundle;
import com.salesbundle.model.DeliveryLog;
import com.salesbundle.model.Subscription;
import com.salesbundle.repository.DeliveryLogRepository;
import com.salesbundle.repository.SubscriptionRepository;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@Slf4j
public class DiscordNotificationService {

    private final DiscordClient discordClient;
    private final SubscriptionRepository subscriptionRepository;
    private final DeliveryLogRepository deliveryLogRepository;

    public DiscordNotificationService(
            @Value("${discord.token}") String token,
            SubscriptionRepository subscriptionRepository,
            DeliveryLogRepository deliveryLogRepository) {
        this.discordClient = DiscordClient.create(token);
        this.subscriptionRepository = subscriptionRepository;
        this.deliveryLogRepository = deliveryLogRepository;
    }

    public void sendNotifications(List<Bundle> bundles) {
        List<Subscription> subscriptions = subscriptionRepository.findAll();

        for (Bundle bundle : bundles) {
            for (Subscription subscription : subscriptions) {
                if (subscription.getDiscordChannelId() == null || subscription.getDiscordChannelId().isEmpty()) {
                    log.warn("Skipping notification for partner {}: No Discord channel ID configured", subscription.getPartnerId());
                    continue;
                }

                if (deliveryLogRepository.findByBundleIdAndPartnerId(bundle.getId(), subscription.getPartnerId()).isPresent()) {
                    continue;
                }

                try {
                    sendEmbed(bundle, subscription);
                    
                    DeliveryLog logEntry = new DeliveryLog();
                    logEntry.setBundleId(bundle.getId());
                    logEntry.setPartnerId(subscription.getPartnerId());
                    deliveryLogRepository.save(logEntry);
                    
                    // Rate limiting: sleep for 2 seconds
                    Thread.sleep(2000);
                    
                } catch (Exception e) {
                    log.error("Failed to send notification for bundle {} to partner {}", bundle.getTitle(), subscription.getPartnerId(), e);
                }
            }
        }
    }

    private void sendEmbed(Bundle bundle, Subscription subscription) {
        String affiliateUrl = String.format("https://www.humblebundle.com%s?partner=%s", bundle.getUrl(), subscription.getPartnerId());
        
        EmbedCreateSpec.Builder embedBuilder = EmbedCreateSpec.builder()
                .color(Color.BLUE)
                .title(bundle.getTitle())
                .url(affiliateUrl)
                .description(bundle.getDescription())
                .timestamp(Instant.now())
                .image(bundle.getHighResImageUrl() != null ? bundle.getHighResImageUrl() : bundle.getImageUrl());

        if (bundle.getAuthor() != null) {
            embedBuilder.author(bundle.getAuthor(), null, null);
        }

        embedBuilder.addField("Ends", (bundle.getEndDate() != null ? bundle.getEndDate().toString() : "N/A"), true);
        embedBuilder.addField("Category", bundle.getCategory(), true);

        if (bundle.getHighlights() != null && !bundle.getHighlights().isEmpty()) {
            embedBuilder.addField("Highlights", String.join("\n", bundle.getHighlights()), false);
        }

        discordClient.getChannelById(Snowflake.of(subscription.getDiscordChannelId()))
                .createMessage(embedBuilder.build().asRequest())
                .subscribe();

        log.info("Sent notification for {} with partner {}", bundle.getTitle(), subscription.getPartnerId());
    }
}
