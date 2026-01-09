package com.salesbundle.controller;

import com.salesbundle.model.Bundle;
import com.salesbundle.repository.BundleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bundles")
public class BundleController {

    private final BundleRepository bundleRepository;

    public BundleController(BundleRepository bundleRepository) {
        this.bundleRepository = bundleRepository;
    }

    @GetMapping
    public List<Bundle> getAllBundles() {
        return bundleRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bundle> getBundleById(@PathVariable Long id) {
        return bundleRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Bundle createBundle(@RequestBody Bundle bundle) {
        return bundleRepository.save(bundle);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bundle> updateBundle(@PathVariable Long id, @RequestBody Bundle bundleDetails) {
        return bundleRepository.findById(id)
                .map(bundle -> {
                    bundle.setTitle(bundleDetails.getTitle());
                    bundle.setDescription(bundleDetails.getDescription());
                    bundle.setMachineName(bundleDetails.getMachineName());
                    bundle.setUrl(bundleDetails.getUrl());
                    bundle.setImageUrl(bundleDetails.getImageUrl());
                    bundle.setHighResImageUrl(bundleDetails.getHighResImageUrl());
                    bundle.setAuthor(bundleDetails.getAuthor());
                    bundle.setCategory(bundleDetails.getCategory());
                    bundle.setEndDate(bundleDetails.getEndDate());
                    bundle.setHighlights(bundleDetails.getHighlights());
                    return ResponseEntity.ok(bundleRepository.save(bundle));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBundle(@PathVariable Long id) {
        if (bundleRepository.existsById(id)) {
            bundleRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
