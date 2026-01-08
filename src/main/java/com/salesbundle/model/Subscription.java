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

    public Subscription() {}

    public Subscription(String partnerId) {
        this.partnerId = partnerId;
    }
}