package com.salesbundle.service;

import com.salesbundle.dto.HumbleResponse;
import com.salesbundle.dto.MosaicSection;
import com.salesbundle.dto.Product;
import com.salesbundle.model.Bundle;
import com.salesbundle.repository.BundleRepository;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.json.JsonMapper;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class BundleIngestionService {

    private final S3Service s3Service;
    private final BundleRepository bundleRepository;
    private final JsonMapper jsonMapper;

    public BundleIngestionService(S3Service s3Service, BundleRepository bundleRepository, JsonMapper jsonMapper) {
        this.s3Service = s3Service;
        this.bundleRepository = bundleRepository;
        this.jsonMapper = jsonMapper;}

    @Transactional
    public List<Bundle> ingestLatestBundleData() {
        Optional<String> latestFile = s3Service.getLatestBundleFile();
        if (latestFile.isEmpty()) {
            log.info("No bundle data found in S3.");
            return List.of();
        }

        String key = latestFile.get();
        log.info("Processing file: {}", key);

        try {
            HumbleResponse response = jsonMapper.readValue(s3Service.readFile(key), HumbleResponse.class);
            List<Bundle> newBundles = new ArrayList<>();

            processCategory(response.getData().getBooks().getMosaic(), "books", newBundles);
            processCategory(response.getData().getGames().getMosaic(), "games", newBundles);
            processCategory(response.getData().getSoftware().getMosaic(), "software", newBundles);

            return newBundles;
        } catch (Exception e) {
            log.error("Error processing bundle data", e);
            throw new RuntimeException(e);
        }
    }

    private void processCategory(List<MosaicSection> sections, String category, List<Bundle> newBundles) {
        if (sections == null) return;

        for (MosaicSection section : sections) {
            if (section.getProducts() == null) continue;

            for (Product product : section.getProducts()) {
                if (bundleRepository.findByMachineName(product.getMachineName()).isPresent()) {
                    continue;
                }

                Bundle bundle = new Bundle();
                bundle.setMachineName(product.getMachineName());
                bundle.setTitle(product.getTitle());
                bundle.setUrl(product.getUrl());
                bundle.setImageUrl(product.getImageUrl());
                bundle.setHighResImageUrl(product.getHighResImageUrl());
                bundle.setDescription(product.getMarketingBlurb());
                bundle.setDetailedDescription(product.getDetailedMarketingBlurb());
                bundle.setAuthor(product.getAuthor());
                bundle.setHighlights(product.getHighlights());
                bundle.setCategory(category);
                
                if (product.getStartDate() != null) {
                    try {
                        bundle.setStartDate(LocalDateTime.parse(product.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    } catch (Exception e) {
                        log.warn("Failed to parse start date: {}", product.getStartDate());
                    }
                }

                if (product.getEndDate() != null) {
                    try {
                        bundle.setEndDate(LocalDateTime.parse(product.getEndDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    } catch (Exception e) {
                        log.warn("Failed to parse end date: {}", product.getEndDate());
                    }
                }

                bundleRepository.save(bundle);
                newBundles.add(bundle);
                log.info("Saved new bundle: {}", bundle.getTitle());
            }
        }
    }
}