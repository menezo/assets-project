package com.menezo.assetsproject.controller;

import com.menezo.assetsproject.model.entities.*;
import com.menezo.assetsproject.model.services.ClientService;
import jakarta.transaction.Transactional;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ClientController {

    @FXML
    private Label clientName;
    @FXML
    private Label currentStockPortfolioWeight;
    @FXML
    private Label desiredStockPortfolioWeight;
    @FXML
    private TableView<Stock> stockPortfolioTableView;
    @FXML
    private TableColumn<Stock, String> stockNameColumn;
    @FXML
    private TableColumn<Stock, String> stockTickerColumn;
    @FXML
    private TableColumn<Stock, Double> stockCurrentPriceColumn;
    @FXML
    private TableColumn<Stock, Double> stockCeilingPriceColumn;
    @FXML
    private TableColumn<Stock, Integer> stockQuantityColumn;
    //@FXML
    //private TableColumn<Stock, Double> stockTotalValueColumn;
    @FXML
    private TableColumn<Stock, Double> stockCurrentWeightColumn;
    //@FXML
    //private TableColumn<Stock, Double> stockDesiredWeightColumn;
    @FXML
    private Button addStockButton;

    @FXML
    private Label currentREITPortfolioWeight;
    @FXML
    private Label desiredREITPortfolioWeight;
    @FXML
    private TableView<REIT> reitPortfolioTableView;
    @FXML
    private TableColumn<REIT, String> reitNameColumn;
    @FXML
    private TableColumn<REIT, String> reitTickerColumn;
    @FXML
    private TableColumn<REIT, Double> reitCurrentPriceColumn;
    @FXML
    private TableColumn<REIT, Double> reitCeilingPriceColumn;
    @FXML
    private TableColumn<REIT, Integer> reitQuantityColumn;
    //@FXML
    //private TableColumn<REIT, Double> reitTotalValueColumn;
    @FXML
    private TableColumn<REIT, Double> reitCurrentWeightColumn;
    //@FXML
    //private TableColumn<REIT, Double> reitDesiredWeightColumn;
    @FXML
    private Button addREITButton;

    @Autowired
    private ClientService clientService;
    private Long clientId;


    @FXML
    public void initialize() {

        stockNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        stockTickerColumn.setCellValueFactory(new PropertyValueFactory<>("ticker"));
        stockCurrentPriceColumn.setCellValueFactory(new PropertyValueFactory<>("currentPrice"));
        stockCeilingPriceColumn.setCellValueFactory(new PropertyValueFactory<>("ceilingPrice"));
        stockQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        stockCurrentWeightColumn.setCellValueFactory(new PropertyValueFactory<>("currentWeight"));

        reitNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        reitTickerColumn.setCellValueFactory(new PropertyValueFactory<>("ticker"));
        reitCurrentPriceColumn.setCellValueFactory(new PropertyValueFactory<>("currentPrice"));
        reitCeilingPriceColumn.setCellValueFactory(new PropertyValueFactory<>("ceilingPrice"));
        reitQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        reitCurrentWeightColumn.setCellValueFactory(new PropertyValueFactory<>("currentWeight"));

    }

    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
        tryLoadClientData();
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
        tryLoadClientData();
    }

    private void tryLoadClientData() {
        if (clientService != null && clientId != null) {
            loadClientData();
        }
    }

    @Transactional
    public void loadClientData() {
        Optional<Client> clientOptional = clientService.getClientById(clientId);
        if(clientOptional.isPresent()) {
            Client client = clientOptional.get();
            clientName.setText(client.getName());

            List<Portfolio> portfolios = clientService.getPortfoliosByClientId(clientId);
            for(Portfolio portfolio : portfolios) {
                if(portfolio.getType().equals(PortfolioType.STOCKS)) {
                    loadStockPortfolioData(portfolio);
                }
                else if(portfolio.getType().equals(PortfolioType.REITS)) {
                    loadREITPortfolioData(portfolio);
                }
            }
        }
    }

    private void loadStockPortfolioData(Portfolio portfolio) {

        List<Asset> assets = portfolio.getAssets();
        List<Stock> stocks = new ArrayList<>();

        for(Asset asset : assets) {
            if(asset instanceof Stock) {
                stocks.add((Stock) asset);
            }
        }
        stockPortfolioTableView.getItems().setAll(stocks);
    }

    private void loadREITPortfolioData(Portfolio portfolio) {

        List<Asset> assets = portfolio.getAssets();
        List<REIT> reits = new ArrayList<>();

        for(Asset asset : assets) {
            if(asset instanceof REIT) {
                reits.add((REIT) asset);
            }
        }
        reitPortfolioTableView.getItems().setAll(reits);
    }
}
