package com.menezo.assetsproject.model.services;

import com.menezo.assetsproject.model.entities.Client;
import com.menezo.assetsproject.model.entities.Portfolio;
import com.menezo.assetsproject.model.repository.ClientRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    private final ClientRepository repository;
    private final PortfolioService portfolioService;

    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);

    @Autowired
    public ClientService(ClientRepository repository, PortfolioService portfolioService) {
        this.repository = repository;
        this.portfolioService = portfolioService;
    }

    @Transactional
    public Optional<Client> getClientById(Long id) {
        try {
            Optional<Client> clientOptional = repository.findById(id);
            if (clientOptional.isPresent()) {
                logger.info("Successfully retrieved client with ID: {}", id);
                return clientOptional;
            } else {
                logger.warn("No client found with ID: {}", id);
            }
        } catch (Exception e) {
            logger.error("Error while retrieving client with ID: {}", id, e);
            throw e;
        }
        return Optional.empty();
    }

    @Transactional
    public List<Client> getClientsByName(String name) {
        try {
            List<Client> clients = repository.findByNameContainingIgnoreCase(name);
            logger.info("Retrieved {} clients from the database with name containing '{}'", clients.size(), name);
            return clients;
        } catch (Exception e) {
            logger.error("Error while retrieving clients by name", e);
            throw e;
        }
    }

    @Transactional
    public List<Portfolio> getPortfoliosByClientId(Long clientId) {
        try {
            Optional<Client> clientOptional = repository.findById(clientId);
            if (clientOptional.isPresent()) {
                List<Portfolio> portfolios = clientOptional.get().getPortfolios();
                logger.info("Successfully retrieved {} portfolios for client with ID: {}", portfolios.size(), clientId);
                return portfolios;
            } else {
                logger.warn("No client found with ID: {}", clientId);
            }
        } catch (Exception e) {
            logger.error("Error while retrieving portfolios for client with ID: {}", clientId, e);
            throw e;
        }
        return List.of();
    }

    @Transactional
    public Client createClient(Client client) {

        if (client == null || client.getName() == null || client.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Client name cannot be null or empty.");
        }

        try {
            Client savedClient = repository.save(client);
            logger.info("Successfully created client with ID: {}", savedClient.getId());
            return savedClient;
        } catch (Exception e) {
            logger.error("Error while creating client", e);
            throw e;
        }
    }

    @Transactional
    public Client updateClient(Long id, Client updatedClient) {
        try {
            Optional<Client> clientOptional = repository.findById(id);
            if (clientOptional.isPresent()) {
                Client existingClient = clientOptional.get();
                existingClient.setName(updatedClient.getName());
                existingClient.setPortfolios(updatedClient.getPortfolios());
                repository.save(existingClient);
                logger.info("Successfully updated client with ID: {}", id);
                return existingClient;
            } else {
                logger.warn("No client found with ID: {}", id);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error while updating client with ID: {}", id, e);
            throw e;
        }
    }

    @Transactional
    public boolean deleteClient(Long id) {
        try {
            Optional<Client> clientOptional = repository.findById(id);
            if (clientOptional.isPresent()) {
                repository.delete(clientOptional.get());
                logger.info("Successfully deleted client with ID: {}", id);
                return true;
            } else {
                logger.warn("No client found with ID: {}", id);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error while deleting client with ID: {}", id, e);
            throw e;
        }
    }

    @Transactional
    public Optional<Client> addPortfolioToClient(Long clientId, Portfolio portfolio) {
        try {
            Optional<Client> clientOptional = repository.findById(clientId);
            if (clientOptional.isPresent()) {
                Client client = clientOptional.get();
                Portfolio savedPortfolio = portfolioService.createPortfolio(portfolio);
                client.getPortfolios().add(savedPortfolio);
                repository.save(client);

                logger.info("Successfully added portfolio to client with ID: {}", clientId);
                return Optional.of(client);
            } else {
                logger.warn("No client found with ID: {}", clientId);
            }
        } catch (Exception e) {
            logger.error("Error while adding portfolio to client with ID: {}", clientId, e);
            throw e;
        }
        return Optional.empty();
    }

    @Transactional
    public Optional<Client> updatePortfolioForClient(Long clientId, Long portfolioId, Portfolio updatedPortfolio) {
        try {
            Optional<Client> clientOptional = repository.findById(clientId);
            if (clientOptional.isPresent()) {
                Client client = clientOptional.get();

                Optional<Portfolio> portfolioOptional = portfolioService.getPortfolioById(portfolioId);
                if (portfolioOptional.isPresent()) {
                    Portfolio portfolio = portfolioOptional.get();

                    portfolio.setType(updatedPortfolio.getType());
                    portfolio.setAssets(updatedPortfolio.getAssets());
                    portfolio.setCurrentWeight(updatedPortfolio.getCurrentWeight());

                    Portfolio updatedPortfolioEntity = portfolioService.updatePortfolio(portfolioId, portfolio);

                    repository.save(client);

                    logger.info("Successfully updated portfolio for client with ID: {}", clientId);
                    return Optional.of(client);
                } else {
                    logger.warn("No portfolio found with ID: {}", portfolioId);
                }
            } else {
                logger.warn("No client found with ID: {}", clientId);
            }
        } catch (Exception e) {
            logger.error("Error while updating portfolio for client with ID: {}", clientId, e);
            throw e;
        }
        return Optional.empty();
    }

    @Transactional
    public Optional<Client> deletePortfolioFromClient(Long clientId, Long portfolioId) {
        if (clientId == null) {
            throw new NullPointerException("Client ID cannot be null");
        }
        if (portfolioId == null) {
            throw new NullPointerException("Portfolio ID cannot be null");
        }

        try {
            Optional<Client> clientOptional = repository.findById(clientId);
            if (clientOptional.isPresent()) {
                Client client = clientOptional.get();
                Optional<Portfolio> portfolioOptional = portfolioService.getPortfolioById(portfolioId);

                if (portfolioOptional.isPresent()) {
                    Portfolio portfolio = portfolioOptional.get();

                    boolean portfolioExists = client.getPortfolios().stream()
                            .anyMatch(p -> p.getId()!= null && p.getId().equals(portfolio.getId()));

                    if (portfolioExists) {
                        boolean deletionResult = portfolioService.deletePortfolio(portfolioId);
                        if (deletionResult) {
                            client.getPortfolios().removeIf(p -> p.getId()!= null && p.getId().equals(portfolioId));
                            repository.save(client);

                            logger.info("Successfully deleted portfolio with ID: {} from client with ID: {}", portfolioId, clientId);
                            return Optional.of(client);
                        } else {
                            logger.warn("Failed to delete portfolio with ID: {} from client with ID: {}", portfolioId, clientId);
                        }
                    } else {
                        logger.warn("Client with ID: {} does not have portfolio with ID: {}", clientId, portfolioId);
                    }
                } else {
                    logger.warn("No portfolio found with ID: {}", portfolioId);
                }
            } else {
                logger.warn("No client found with ID: {}", clientId);
            }
        } catch (Exception e) {
            logger.error("Error while deleting portfolio from client with ID: {}", clientId, e);
            throw e;
        }
        return Optional.empty();
    }


    @Transactional
    public List<Client> getAllClients() {
        try {
            return repository.findAll();
        }
        catch(Exception e) {
            logger.error("Error while getting all the clients", e);
            throw e;
        }
    }
}
