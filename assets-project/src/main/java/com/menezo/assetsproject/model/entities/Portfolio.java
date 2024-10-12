package com.menezo.assetsproject.model.entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table (name = "portfolios")
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PortfolioType type;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    private List<Asset> assets;

    @Column(nullable = false)
    private Double currentWeight;

    public Portfolio() {
    }

    public Portfolio(PortfolioType type, List<Asset> assets, Double weight) {
        this.type = type;
        this.assets = assets;
        this.currentWeight = weight;
    }

    public Long getId() {
        return id;
    }

    public PortfolioType getType() {
        return type;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public Double getCurrentWeight() {
        return currentWeight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Portfolio portfolio = (Portfolio) o;
        return Objects.equals(id, portfolio.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
