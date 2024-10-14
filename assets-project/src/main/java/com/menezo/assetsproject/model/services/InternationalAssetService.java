package com.menezo.assetsproject.model.services;

import com.menezo.assetsproject.model.dto.AssetDTO;
import com.menezo.assetsproject.model.entities.InternationalAsset;
import com.menezo.assetsproject.model.repository.InternationalAssetRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InternationalAssetService {

    private final InternationalAssetRepository repository;
    private final AssetApiClient assetApiClient;
    private static final Logger logger = LoggerFactory.getLogger(InternationalAssetService.class);

    @Autowired
    public InternationalAssetService(InternationalAssetRepository repository, AssetApiClient assetApiClient) {
        this.repository = repository;
        this.assetApiClient = assetApiClient;
    }

    @Transactional
    public void updateInternationalAssetsPrices() {
        List<InternationalAsset> internationalAssets = repository.findAll();
        List<InternationalAsset> updateInternationalAssets = new ArrayList<>();
        for(InternationalAsset asset : internationalAssets) {
            Optional<AssetDTO> intAssetDataOptional = assetApiClient.getAssetData(asset.getTicker());

            if(intAssetDataOptional.isPresent()) {
                AssetDTO assetData = intAssetDataOptional.get();
                asset.updatePrice(assetData.getRegularMarketPrice());
                updateInternationalAssets.add(asset);
                logger.info("Updated price for stock: {}", asset.getTicker());
            }
            else {
                logger.warn("No data found for stock: {}", asset.getTicker());
            }
        }
        if(!updateInternationalAssets.isEmpty()) {
            repository.saveAll(updateInternationalAssets);
        }
    }

    @Transactional
    public void addInternationalAssetByTicker(String ticker, double ceilingPrice) {
        try {
            if(repository.findByTicker(ticker).isPresent()) {
                logger.info("International asset already exists in system.");
            }
            else {
                Optional<AssetDTO> intAssetDataOptional = assetApiClient.getAssetData(ticker);
                if(intAssetDataOptional.isPresent()){
                    AssetDTO intAssetData = intAssetDataOptional.get();
                    InternationalAsset asset = new InternationalAsset(ticker, intAssetData.getLongName(), intAssetData.getRegularMarketPrice(), ceilingPrice, 0, 0);
                    repository.save(asset);
                    logger.info("Successfully saved international asset: {} with price: {}", asset.getTicker(), asset.getCurrentPrice());
                }
                else {
                    logger.warn("No data found on API for international asset: {}", ticker);
                }
            }
        }
        catch (Exception e) {
            logger.error("Error while adding international asset: {}", ticker, e);
            throw e;
        }
    }

}
