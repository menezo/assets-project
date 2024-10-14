package com.menezo.assetsproject.model.repository;

import com.menezo.assetsproject.model.entities.InternationalAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InternationalAssetRepository extends JpaRepository<InternationalAsset, Long> {
    Optional<InternationalAsset> findByTicker(String ticker);
}
