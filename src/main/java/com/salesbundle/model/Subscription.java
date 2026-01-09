package com.salesbundle.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String partnerId;

    private String discordChannelId;

    public Subscription() {}

    public Subscription(String partnerId, String discordChannelId) {
        this.partnerId = partnerId;
        this.discordChannelId = discordChannelId;
    }
}