package com.menezo.assetsproject.controller;

import com.menezo.assetsproject.model.entities.Client;
import com.menezo.assetsproject.model.services.ClientService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

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

    @FXML
    public void initialize() {

        clientNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        loadClientData();

        clientTableView.setOnMouseClicked(event -> handleClientDoubleClick(event));

    }

    private void loadClientData() {

        clientService.createClient(new Client("Joao", null));

        List<Client> clients = clientService.getAllClients();
        clientTableView.getItems().setAll(clients);
    }

    private void handleClientDoubleClick(MouseEvent event) {
        if(event.getClickCount() == 2) {
            Client selectedClient = clientTableView.getSelectionModel().getSelectedItem();
            System.out.println("CLIENTE SELECIONADO");
        }
    }
}
