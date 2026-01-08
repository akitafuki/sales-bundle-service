package com.salesbundle.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Bundle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String machineName; // Unique identifier from JSON

    private String title;

    private String url;

    private String imageUrl;

    private String highResImageUrl;

    @Column(length = 1000)
    private String description; // marketing_blurb

    @Column(columnDefinition = "TEXT")
    private String detailedDescription; // detailed_marketing_blurb

    private String author;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> highlights;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String category; // games, books, software

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}