package com.menezo.assetsproject.model.repository;

import com.menezo.assetsproject.model.entities.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    //Find stocks that current price is less than the ceiling price
    List<Stock> findByCurrentPriceLessThan(double ceilingPrice);
    Optional<Stock> findByTicker(String ticker);
}
