package com.menezo.assetsproject.model.repository;

import com.menezo.assetsproject.model.entities.REIT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface REITRepository extends JpaRepository<REIT, Long> {
    //find by yield greater than??
    Optional<REIT> findByTicker(String ticker);
}
