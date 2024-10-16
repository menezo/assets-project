package com.menezo.assetsproject.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table (name = "international_assets")
public class InternationalAsset extends Asset {

    public InternationalAsset() {
    }

    public InternationalAsset(String ticker, String name, double currentPrice, double ceilingPrice, double currentWeight, int quantity) {
        super(ticker, name, currentPrice, ceilingPrice, currentWeight, quantity);
    }
}
