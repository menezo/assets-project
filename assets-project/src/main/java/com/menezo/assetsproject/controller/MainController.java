package com.menezo.assetsproject.controller;

import com.menezo.assetsproject.model.entities.*;
import com.menezo.assetsproject.model.services.ClientService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    @FXML
    private TableView<Client> clientTableView;
    @FXML
    private TableColumn<Client, String> clientNameColumn;

    //@FXML
    //private TableColumn<Client, String> portfolioStatusColumn;

    @FXML
    private Button addClientButton;

    @FXML
    private Button importClientsButton;

    @FXML
    private Button exportClientsButton;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ApplicationContext applicationContext;

    @FXML
    public void initialize() {

        clientNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        loadClientData();

        clientTableView.setOnMouseClicked(event -> handleClientDoubleClick(event));

    }

    private void loadClientData() {

        Portfolio stockPortfolio = new Portfolio(PortfolioType.STOCKS, new ArrayList<>(), 10.0);

        Asset stock = new Stock("AGRO3", "Brasilagro - Companhia Brasileira de Propriedades Agrícolas (BVMF:AGRO3)", 50, 60, 20, 10);
        stock.setPortfolio(stockPortfolio);

        Asset stock2 = new Stock("ALUP11", "ALUPAR UNT (BVMF:ALUP11)", 30, 10, 30, 100);
        stock2.setPortfolio(stockPortfolio);

        stockPortfolio.getAssets().add(stock);
        stockPortfolio.getAssets().add(stock2);

        Client client = new Client("Joao");
        client.addPortfolio(stockPortfolio);

        clientService.createClient(client);

        List<Client> clients = clientService.getAllClients();
        clientTableView.getItems().setAll(clients);
    }

    private void handleClientDoubleClick(MouseEvent event) {
        if(event.getClickCount() == 2) {
            Client selectedClient = clientTableView.getSelectionModel().getSelectedItem();
            loadClientPortfolio(selectedClient);
            System.out.println("CLIENTE SELECIONADO");
        }
    }

    private void loadClientPortfolio(Client selectedClient) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client.fxml"));
            Parent clientRoot = loader.load();

            ClientController clientController = loader.getController();
            clientController.setClientService(clientService);
            clientController.setClientId(selectedClient.getId());

            Stage stage = new Stage();
            stage.setScene(new Scene(clientRoot));
            stage.setTitle("Client Portfolio");
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
