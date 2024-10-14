package com.menezo.assetsproject.model.services;

import com.menezo.assetsproject.model.dto.AssetDTO;
import com.menezo.assetsproject.model.entities.Stock;
import com.menezo.assetsproject.model.repository.StockRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StockService {

    private final StockRepository repository;
    private final AssetApiClient assetApiClient;
    private static final Logger logger = LoggerFactory.getLogger(StockService.class);

    @Autowired
    public StockService(StockRepository repository, AssetApiClient assetApiClient) {
        this.repository = repository;
        this.assetApiClient = assetApiClient;
    }

    @Transactional
    public void updateStockPrices() {
        List<Stock> stocks = repository.findAll();
        List<Stock> updateStocks = new ArrayList<>();
        for(Stock stock : stocks) {
            Optional<AssetDTO> stockDataOptional = assetApiClient.getAssetData(stock.getTicker());

            if(stockDataOptional.isPresent()) {
                AssetDTO stockData = stockDataOptional.get();
                stock.updatePrice(stockData.getRegularMarketPrice());
                updateStocks.add(stock);
                logger.info("Updated price for stock: {}", stock.getTicker());
            }
            else {
                logger.warn("No data found for stock: {}", stock.getTicker());
            }
        }
        if(!updateStocks.isEmpty()) {
            repository.saveAll(updateStocks);
        }
    }

    //Add verification to calculate the current weight after adding this stock
    @Transactional
    public void addStockByTicker(String ticker, double ceilingPrice) {
        try {
            if(repository.findByTicker(ticker).isPresent()) {
                logger.info("Stock already exists in system.");
            }
            else {
                Optional<AssetDTO> stockDataOptional = assetApiClient.getAssetData(ticker);
                if(stockDataOptional.isPresent()) {
                    AssetDTO stockData = stockDataOptional.get();
                    Stock stock = new Stock(ticker, stockData.getLongName(), stockData.getRegularMarketPrice(), ceilingPrice, 0, 0);
                    repository.save(stock);
                    logger.info("Successfully saved stock: {} with price: {}", stock.getTicker(), stock.getCurrentPrice());
                }
                else {
                    logger.warn("No data found on API for stock: {}", ticker);
                }
            }
        }
        catch (Exception e) {
            logger.error("Error while adding stock: {}", ticker, e);
            throw e;
        }
    }

    @Transactional
    public boolean deleteStockByTicker(String ticker) {
        try {
            Optional<Stock> stock = repository.findByTicker(ticker);
            if(stock.isPresent()) {
                //Add call to portfolio service to verify if this stock is present in any portfolio to avoid deleting in cascade
                //portfolioService.isStockPresentInAnyPortfolio(stock.getId());
                repository.deleteById(stock.get().getId());
                logger.info("Successfully deleted Stock: {}", ticker);
                return true;
            }
            else {
                logger.warn("No data found for stock: {}", ticker);
                return false;
            }
        }
        catch (Exception e) {
            logger.error("Error while deleting stock: {}", ticker, e);
            throw e;
        }
    }

    @Transactional
    public List<Stock> getAllStocks() {
        try {
            List<Stock> stocks = repository.findAll();
            logger.info("Retrieved {} stocks from the database.", stocks.size());
            return stocks;
        }
        catch (Exception e) {
            logger.error("Error while getting stocks", e);
            throw e;
        }
    }
}
