package ui.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Compte;
import model.CompteCourant;
import model.CompteEpargne;
import model.BanqueService;

import java.io.IOException;
import java.util.List;

public class CompteController {

    private BanqueService banqueService;
    private Stage primaryStage;

    // Form fields
    @FXML private TextField clientIdField;
    @FXML private TextField soldeField;
    @FXML private ChoiceBox<String> typeChoice;

    // Table
    @FXML private TableView<Compte> comptesTable;
    @FXML private TableColumn<Compte, Number> numColumn;
    @FXML private TableColumn<Compte, Number> soldeColumn;
    @FXML private TableColumn<Compte, Number> clientColumn;
    @FXML private TableColumn<Compte, String> typeColumn;

    public void setBanqueService(BanqueService banqueService) {
        this.banqueService = banqueService;
        loadComptes(); // load from DB/CSV when screen opens
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    @FXML
    private void initialize() {
        // Default type
        if (typeChoice != null) {
            typeChoice.getItems().addAll("COURANT", "EPARGNE");
            typeChoice.setValue("COURANT");
        }

        // Table column bindings
        numColumn.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getNumero()));
        soldeColumn.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().getSolde()));
        clientColumn.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getClientId()));
        typeColumn.setCellValueFactory(d ->
                new SimpleStringProperty(
                        (d.getValue() instanceof CompteCourant) ? "COURANT" : "EPARGNE"
                ));
    }

    private void loadComptes() {
        if (banqueService == null) return;
        try {
            List<Compte> comptes = banqueService.listerComptes(); // from DB via DAO SQL
            comptesTable.setItems(FXCollections.observableArrayList(comptes));
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,
                    "Erreur chargement comptes: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void creerCompte() {
        try {
            int clientId = Integer.parseInt(clientIdField.getText().trim());
            double soldeInitial = Double.parseDouble(soldeField.getText().trim());
            String type = typeChoice.getValue();

            Compte compte;
            if ("COURANT".equalsIgnoreCase(type)) {
                compte = banqueService.creerCompteCourant(clientId, soldeInitial);
            } else {
                compte = banqueService.creerCompteEpargne(clientId, soldeInitial);
            }

            comptesTable.getItems().add(compte); // update UI
            soldeField.clear();

        } catch (NumberFormatException ex) {
            new Alert(Alert.AlertType.WARNING,
                    "ClientId et solde doivent être numériques").showAndWait();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,
                    "Erreur création compte: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void backToDashboard() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/AdminDashboard.fxml"));
        Scene scene = new Scene(loader.load());
        AdminDashboardController controller = loader.getController();
        controller.setBanqueService(banqueService);
        controller.setPrimaryStage(primaryStage);
        primaryStage.setScene(scene);
    }
}
