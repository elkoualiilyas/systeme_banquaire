package ui.controller;


import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Operation;
import model.BanqueService;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistoriqueOperationsController {

    private BanqueService banqueService;
    private Stage primaryStage;

    @FXML private TextField numeroCompteField;
    @FXML private TableView<Operation> operationsTable;
    @FXML private TableColumn<Operation, Number> idColumn;
    @FXML private TableColumn<Operation, String> typeColumn;
    @FXML private TableColumn<Operation, Number> montantColumn;
    @FXML private TableColumn<Operation, String> dateColumn;
    @FXML private TableColumn<Operation, Number> compteSourceColumn;
    @FXML private TableColumn<Operation, Number> compteDestColumn;

    public void setBanqueService(BanqueService banqueService) {
        this.banqueService = banqueService;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(d -> 
            new SimpleIntegerProperty(d.getValue().getId()));
        
        typeColumn.setCellValueFactory(d -> 
            new SimpleStringProperty(d.getValue().getType().name()));
        montantColumn.setCellValueFactory(d -> 
            new SimpleDoubleProperty(d.getValue().getMontant()));
        
        dateColumn.setCellValueFactory(d -> 
            new SimpleObjectProperty<>(
                d.getValue().getDate().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                )
            )
        );
        
        compteSourceColumn.setCellValueFactory(d -> 
            new SimpleIntegerProperty(d.getValue().getCompteSource())); 

        compteDestColumn.setCellValueFactory(d -> {
            Integer dest = d.getValue().getCompteDestination(); 
            return new SimpleIntegerProperty(dest != null ? dest : 0);
        });
    }


    @FXML
    private void chargerHistorique() {
        try {
            int numeroCompte = Integer.parseInt(numeroCompteField.getText().trim());
            List<Operation> operations = banqueService.historiqueOperations(numeroCompte);
            operationsTable.setItems(FXCollections.observableArrayList(operations));
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.WARNING, "Numéro de compte invalide").showAndWait();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur chargement: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void backToDashboard() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/AdminDashboard.fxml"));
        Scene scene = new Scene(loader.load());
        AdminDashboardController controller = loader.getController();
        controller.setBanqueService(banqueService);
        controller.setPrimaryStage(primaryStage);
        primaryStage.setScene(scene);
    }
}
