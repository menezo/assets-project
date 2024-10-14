package com.menezo.assetsproject.model.services;

import com.menezo.assetsproject.model.dto.AssetDTO;
import com.menezo.assetsproject.model.entities.InternationalAsset;
import com.menezo.assetsproject.model.entities.REIT;
import com.menezo.assetsproject.model.repository.InternationalAssetRepository;
import com.menezo.assetsproject.model.repository.REITRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class REITService {

    private final REITRepository repository;
    private final AssetApiClient assetApiClient;
    private static final Logger logger = LoggerFactory.getLogger(REITService.class);

    @Autowired
    public REITService(REITRepository repository, AssetApiClient assetApiClient) {
        this.repository = repository;
        this.assetApiClient = assetApiClient;
    }

    @Transactional
    public void updateREITPrices() {
        List<REIT> reits = repository.findAll();
        List<REIT> updateReits = new ArrayList<>();
        for(REIT reit : reits) {
            Optional<AssetDTO> reitDataOptional = assetApiClient.getAssetData(reit.getTicker());

            if(reitDataOptional.isPresent()) {
                AssetDTO reitData = reitDataOptional.get();
                reit.updatePrice(reitData.getRegularMarketPrice());
                updateReits.add(reit);
                logger.info("Updated price for stock: {}", reit.getTicker());
            }
            else {
                logger.warn("No data found for stock: {}", reit.getTicker());
            }
        }
        if(!updateReits.isEmpty()) {
            repository.saveAll(updateReits);
        }
    }

    @Transactional
    public void addREITByTicker(String ticker, double ceilingPrice) {
        try {
            if(repository.findByTicker(ticker).isPresent()) {
                logger.info("REIT already exists in system.");
            }
            else {
                Optional<AssetDTO> reitDataOptional = assetApiClient.getAssetData(ticker);
                if(reitDataOptional.isPresent()){
                    AssetDTO reitData = reitDataOptional.get();
                    REIT reit = new REIT(ticker, reitData.getLongName(), reitData.getRegularMarketPrice(), ceilingPrice, 0, 0);
                    repository.save(reit);
                    logger.info("Successfully saved REIT: {} with price: {}", reit.getTicker(), reit.getCurrentPrice());
                }
                else {
                    logger.warn("No data found on API for REIT: {}", ticker);
                }
            }
        }
        catch (Exception e) {
            logger.error("Error while adding REIT: {}", ticker, e);
            throw e;
        }
    }

}
