package com.menezo.assetsproject.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "stocks")
public class Stock extends Asset {

    public Stock() {
        super();
    }

    public Stock(String ticker, String name, double currentPrice, double ceilingPrice, double currentWeight, int quantity) {
        super(ticker, name, currentPrice, ceilingPrice, currentWeight, quantity);
    }
}
