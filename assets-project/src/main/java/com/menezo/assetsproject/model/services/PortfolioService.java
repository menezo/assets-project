package com.menezo.assetsproject.model.services;

import com.menezo.assetsproject.model.entities.Portfolio;
import com.menezo.assetsproject.model.entities.PortfolioType;
import com.menezo.assetsproject.model.repository.PortfolioRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PortfolioService {

    private final PortfolioRepository repository;

    private static final Logger logger = LoggerFactory.getLogger(PortfolioService.class);

    @Autowired
    public PortfolioService(PortfolioRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Optional<Portfolio> getPortfolioById(Long id) {
        try {
            Optional<Portfolio> portfolioOptional = repository.findById(id);
            if(portfolioOptional.isPresent()) {
                logger.info("Retrieved {} portfolio from the database.", portfolioOptional.get());
                return portfolioOptional;
            }
        }
        catch (Exception e){
            logger.error("Error while getting portfolio", e);
            throw e;
        }
        return Optional.empty();
    }

    @Transactional
    public List<Portfolio> getPorfoliosByType(PortfolioType type) {
        List<Portfolio> portfolios = new ArrayList<>();
        try {
            portfolios = repository.findByType(type);
            if(!portfolios.isEmpty()) {
                logger.info("Retrieved {} portfolios from the database.", portfolios.size());
                return portfolios;
            }
            else {
                logger.info("There are no portfolios in the database");
            }
        }
        catch (Exception e) {
            logger.error("Error while getting portfolios", e);
            throw e;
        }
        return portfolios;
    }

    @Transactional
    public List<Portfolio> getPortfoliosByAssetsTicker(String ticker) {
        List<Portfolio> portfolios = new ArrayList<>();
        try {
            portfolios = repository.findByAssets_Ticker(ticker);
            if(!portfolios.isEmpty()) {
                logger.info("Retrieved {} portfolios from the database.", portfolios.size());
                return portfolios;
            }
            else {
                logger.info("There are no portfolios with the specific asset in the database");
            }
        }
        catch (Exception e) {
            logger.error("Error while getting portfolios by assets ticker", e);
            throw e;
        }
        return portfolios;
    }

    @Transactional
    public List<Portfolio> getPortfolioWeightGreater(Double weight) {
        List<Portfolio> portfolios = new ArrayList<>();
        try {
            portfolios = repository.findByCurrentWeightGreaterThan(weight);
            if(!portfolios.isEmpty()) {
                logger.info("Retrieved {} portfolios from the database.", portfolios.size());
                return portfolios;
            }
            else {
                logger.info("There are no portfolios with weight greater than " + weight);
            }
        }
        catch(Exception e) {
            logger.error("Error while getting portfolios with wight greater than " + weight, e);
            throw e;
        }
        return portfolios;
    }

    @Transactional
    public List<Portfolio> getPortfolioWeightLess(Double weight) {
        List<Portfolio> portfolios = new ArrayList<>();
        try {
            portfolios = repository.findByCurrentWeightLessThan(weight);
            if(!portfolios.isEmpty()) {
                logger.info("Retrieved {} portfolios from the database.", portfolios.size());
                return portfolios;
            }
            else {
                logger.info("There are no portfolios with weight less than " + weight);
            }
        }
        catch(Exception e) {
            logger.error("Error while getting portfolios with wight less than " + weight, e);
            throw e;
        }
        return portfolios;
    }

    @Transactional
    public Portfolio createPortfolio(Portfolio portfolio) {
        try {
            Portfolio savedPortfolio = repository.save(portfolio);
            logger.info("Successfully created portfolio with ID: {}", savedPortfolio.getId());
            return savedPortfolio;
        } catch (Exception e) {
            logger.error("Error while creating portfolio", e);
            throw e;
        }
    }

    @Transactional
    public Portfolio updatePortfolio(Long id, Portfolio updatedPortfolio) {
        try {
            Optional<Portfolio> portfolioOptional = repository.findById(id);
            if (portfolioOptional.isPresent()) {
                Portfolio existingPortfolio = portfolioOptional.get();
                existingPortfolio.setType(updatedPortfolio.getType());
                existingPortfolio.setAssets(updatedPortfolio.getAssets());
                existingPortfolio.setCurrentWeight(updatedPortfolio.getCurrentWeight());
                repository.save(existingPortfolio);
                logger.info("Successfully updated portfolio with ID: {}", id);
                return existingPortfolio;
            } else {
                logger.warn("No portfolio found with ID: {}", id);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error while updating portfolio with ID: {}", id, e);
            throw e;
        }
    }

    @Transactional
    public boolean deletePortfolio(Long id) {
        try {
            if (repository.existsById(id)) {
                repository.deleteById(id);
                logger.info("Successfully deleted portfolio with ID: {}", id);
                return true;
            } else {
                logger.warn("No portfolio found with ID: {}", id);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error while deleting portfolio with ID: {}", id, e);
            throw e;
        }
    }
}
