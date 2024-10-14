package com.menezo.assetsproject.model.services;

import com.menezo.assetsproject.model.dto.StockDTO;
import com.menezo.assetsproject.model.entities.Stock;
import com.menezo.assetsproject.model.repository.StockRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StockService {

    private final StockRepository repository;
    private final StockApiClient stockApiClient;
    private static final Logger logger = LoggerFactory.getLogger(StockService.class);

    @Autowired
    public StockService(StockRepository repository, StockApiClient stockApiClient) {
        this.repository = repository;
        this.stockApiClient = stockApiClient;
    }

    @Transactional
    public void updateStockPrices() {
        List<Stock> stocks = repository.findAll();
        for(Stock stock : stocks) {
            Optional<StockDTO> stockDataOptional = stockApiClient.getStockData(stock.getTicker());

            if(stockDataOptional.isPresent()) {
                StockDTO stockData = stockDataOptional.get();
                stock.updatePrice(stockData.getRegularMarketPrice());
                repository.save(stock);
                logger.info("Updated price for stock: {}", stock.getTicker());
            }
            else {
                logger.warn("No data found for stock: {}", stock.getTicker());
            }
        }
    }
}
