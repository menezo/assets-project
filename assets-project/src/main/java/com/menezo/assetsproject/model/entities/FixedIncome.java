package com.menezo.assetsproject.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table (name = "fixed_income")
public class FixedIncome extends Asset {

    public FixedIncome() {
    }

    public FixedIncome(String ticker, String name, double currentPrice, double ceilingPrice, double currentWeight, int quantity) {
        super(ticker, name, currentPrice, ceilingPrice, currentWeight, quantity);
    }
}
