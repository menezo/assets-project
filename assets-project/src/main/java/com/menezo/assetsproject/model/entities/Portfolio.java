package com.menezo.assetsproject.model.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table (name = "portfolio")
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type; // Could be Stocks, REITs, FixedIncome...

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    private List<Asset> assets;

    @Column(nullable = false)
    private Double length;
}
