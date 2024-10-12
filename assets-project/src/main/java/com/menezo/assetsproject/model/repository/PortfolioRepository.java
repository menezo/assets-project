package com.menezo.assetsproject.model.repository;

import com.menezo.assetsproject.model.entities.Portfolio;
import com.menezo.assetsproject.model.entities.PortfolioType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    List<Portfolio> findByType(PortfolioType type);
    //Find portfolios that are above or below the established weight
    List<Portfolio> findByCurrentWeightGreaterThan(Double weight);
    List<Portfolio> findByCurrentWeightLessThan(Double weight);
    //Find portfolios that contain that specific ticker
    List<Portfolio> findByAssets_Ticker(String ticker);

}
