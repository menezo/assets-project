package com.menezo.assetsproject.model.entities;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)  // Using inheritance with separated tables
@Table(name = "assets")
public abstract class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String ticker;
    @Column
    private String name;
    @Column(nullable = false)
    private double currentPrice;

    @Column(nullable = false)
    private double ceilingPrice;
    @Column(nullable = false)
    private double currentWeight;
    @Column
    private int quantity;
}
