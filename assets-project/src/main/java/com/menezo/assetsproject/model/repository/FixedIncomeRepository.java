package com.menezo.assetsproject.model.repository;

import com.menezo.assetsproject.model.entities.FixedIncome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FixedIncomeRepository extends JpaRepository<FixedIncome, Long> {
    //find by date?
}
