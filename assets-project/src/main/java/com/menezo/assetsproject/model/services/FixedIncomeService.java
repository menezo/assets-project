package com.menezo.assetsproject.model.services;

import com.menezo.assetsproject.model.dto.AssetDTO;
import com.menezo.assetsproject.model.entities.FixedIncome;
import com.menezo.assetsproject.model.repository.FixedIncomeRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FixedIncomeService {

    private final FixedIncomeRepository repository;
    private final AssetApiClient assetApiClient;

    private static final Logger logger = LoggerFactory.getLogger(FixedIncomeService.class);

    @Autowired
    public FixedIncomeService(FixedIncomeRepository repository, AssetApiClient assetApiClient) {
        this.repository = repository;
        this.assetApiClient = assetApiClient;
    }

    @Transactional
    public List<FixedIncome> getAllFixedIncomes() {
        try {
            List<FixedIncome> fixedIncomes = repository.findAll();
            logger.info("Retrieved {} fixed incomes from the database.", fixedIncomes.size());
            return fixedIncomes;
        }
        catch (Exception e) {
            logger.error("Error while getting fixed incomes", e);
            throw e;
        }
    }

    @Transactional
    public Optional<FixedIncome> findFixedIncomeById(Long id) {
        try {
            Optional<FixedIncome> fixedIncome = repository.findById(id);
            if(fixedIncome.isPresent()) {
                logger.info("Found Fixed Income with id: " + id);
            }
            else {
                logger.info("No data found for fixed income: {}", id);
            }
            return fixedIncome;
        }
        catch (Exception e) {
            logger.error("Error while getting fixed income", e);
            throw e;
        }
    }

    @Transactional
    public boolean deleteFixedIncomeByTicker(String ticker) {
        try {
            Optional<FixedIncome> fixedIncome = repository.findByTicker(ticker);
            if(fixedIncome.isPresent()) {
                //Add call to portfolio service to verify if this fixed income is present in any portfolio to avoid deleting in cascade
                //portfolioService.isFixedIncomePresentInAnyPortfolio(fixedIncome.getId());
                repository.deleteById(fixedIncome.get().getId());
                logger.info("Successfully deleted Fixed Income: {}", ticker);
                return true;
            }
            else {
                logger.warn("No data found for fixed income: {}", ticker);
                return false;
            }
        }
        catch (Exception e) {
            logger.error("Error while deleting fixed income: {}", ticker, e);
            throw e;
        }
    }

    @Transactional
    public void addFixedIncomeByTicker(String ticker, long ceilingPrice) {
        try {
            if (repository.findByTicker(ticker).isPresent()) {
                logger.info("Fixed Income already exists in system.");
                return;
            }
            Optional<AssetDTO> fixedIncomeDataOptional = assetApiClient.getAssetData(ticker);
            if (fixedIncomeDataOptional.isPresent()) {
                AssetDTO fixedIncomeData = fixedIncomeDataOptional.get();
                FixedIncome fixedIncome = new FixedIncome(ticker, fixedIncomeData.getLongName(), fixedIncomeData.getRegularMarketPrice(), ceilingPrice, 0, 0);
                repository.save(fixedIncome);
                logger.info("Successfully saved fixed income: {} with price: {}", fixedIncome.getTicker(), fixedIncome.getCurrentPrice());
            } else {
                logger.warn("No data found on API for fixed income: {}", ticker);
            }
        } catch (Exception e) {
            logger.error("Error while adding fixed income: {}", ticker, e);
            throw e;
        }
    }
}
