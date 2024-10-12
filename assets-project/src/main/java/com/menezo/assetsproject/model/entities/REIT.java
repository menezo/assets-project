package com.menezo.assetsproject.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "reits")
public class REIT extends Asset {

    public REIT() {
    }

    public REIT(String ticker, String name, double currentPrice, double ceilingPrice, double currentWeight, int quantity) {
        super(ticker, name, currentPrice, ceilingPrice, currentWeight, quantity);
    }
}
