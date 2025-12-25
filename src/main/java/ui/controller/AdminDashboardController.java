package ui.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.BanqueService;
import model.Client;

public class AdminDashboardController {

    private BanqueService banqueService;
    private Stage primaryStage;

    // champs du formulaire
    @FXML private TextField cinField;
    @FXML private TextField nomField;
    @FXML private TextField prenomField;

    // tableau des clients
    @FXML private TableView<Client> clientsTable;
    @FXML private TableColumn<Client, Number> idColumn;
    @FXML private TableColumn<Client, String> cinColumn;
    @FXML private TableColumn<Client, String> nomColumn;
    @FXML private TableColumn<Client, String> prenomColumn;

    public void setBanqueService(BanqueService banqueService) {
        this.banqueService = banqueService;
        loadClients();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getId()));
        cinColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCin()));
        nomColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNom()));
        prenomColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPrenom()));
    }

    private void loadClients() {
        if (banqueService == null) return;
        try {
            var clients = banqueService.listerClients();
            clientsTable.setItems(FXCollections.observableArrayList(clients));
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,
                    "Erreur chargement clients: " + e.getMessage()).showAndWait();
        }
    }


    @FXML
    private void creerClient() {
        String cin = cinField.getText().trim();
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();

        if (cin.isEmpty() || nom.isEmpty() || prenom.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Tous les champs sont obligatoires").showAndWait();
            return;
        }

        try {
            Client c = banqueService.creerClient(cin, nom, prenom);
            clientsTable.getItems().add(c);
            cinField.clear();
            nomField.clear();
            prenomField.clear();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur création client: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void exportClientsCsv() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Exporter clients en CSV");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
        File file = chooser.showSaveDialog(primaryStage);
        if (file == null) return;

        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
            writer.write("id;cin;nom;prenom");
            writer.newLine();
            for (Client c : clientsTable.getItems()) {
                writer.write(c.getId() + ";" +
                             c.getCin() + ";" +
                             c.getNom() + ";" +
                             c.getPrenom());
                writer.newLine();
            }
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Erreur export CSV: " + e.getMessage()).showAndWait();
        }
    }
    @FXML
    private void openComptes() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/ui/CompteView.fxml"));
        Scene scene = new Scene(loader.load());

        // Get the comptes controller and inject dependencies
        CompteController controller = loader.getController();
        controller.setBanqueService(banqueService);
        // if you need back navigation:
        controller.setPrimaryStage(primaryStage);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
    @FXML
    private void openOperations() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/ui/OperationsView.fxml"));
        Scene scene = new Scene(loader.load());

        // Récupérer le controller des opérations
        OperationsController controller = loader.getController();
        controller.setBanqueService(banqueService);   // même service que pour comptes
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
}
