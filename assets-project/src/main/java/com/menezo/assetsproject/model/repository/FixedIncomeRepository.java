package com.menezo.assetsproject.model.repository;

import com.menezo.assetsproject.model.entities.FixedIncome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FixedIncomeRepository extends JpaRepository<FixedIncome, Long> {
    //find by date?
    Optional<FixedIncome> findByTicker(String ticker);
}
