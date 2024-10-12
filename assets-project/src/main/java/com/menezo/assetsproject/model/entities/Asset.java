package com.menezo.assetsproject.model.entities;

import jakarta.persistence.*;

import java.util.Objects;

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

    protected Asset() {
    }

    protected Asset(String ticker, String name, double currentPrice, double ceilingPrice, double currentWeight, int quantity) {
        this.ticker = ticker;
        this.name = name;
        this.currentPrice = currentPrice;
        this.ceilingPrice = ceilingPrice;
        this.currentWeight = currentWeight;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public String getTicker() {
        return ticker;
    }

    public String getName() {
        return name;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public double getCeilingPrice() {
        return ceilingPrice;
    }

    public double getCurrentWeight() {
        return currentWeight;
    }

    public int getQuantity() {
        return quantity;
    }

    public void updatePrice(double newPrice) {
        this.currentPrice = newPrice;
    }

    public void updateAsset(double newPrice, double newWeight, int newQuantity) {
        this.currentPrice = newPrice;
        this.currentWeight = newWeight;
        this.quantity = newQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Asset asset = (Asset) o;
        return Objects.equals(id, asset.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
