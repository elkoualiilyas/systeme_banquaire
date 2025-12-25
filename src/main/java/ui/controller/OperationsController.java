package ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.BanqueService;   // <-- use service.BanqueService, not model.BanqueService

public class OperationsController {

    private BanqueService banqueService;
    private Stage primaryStage;   // <-- define it

    @FXML private TextField compteSourceField;
    @FXML private TextField compteDestField;
    @FXML private TextField montantField;
    @FXML private ChoiceBox<String> typeOperationChoice;
    @FXML private Label messageLabel;

    public void setBanqueService(BanqueService banqueService) {
        this.banqueService = banqueService;
    }

    // called from AdminDashboardController.openOperations(...)
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void initialize() {
        typeOperationChoice.getItems().addAll("DEPOT", "RETRAIT", "VIREMENT");
        typeOperationChoice.setValue("DEPOT");
    }

    @FXML
    private void executerOperation() {
        try {
            String type = typeOperationChoice.getValue();
            int numeroSource = Integer.parseInt(compteSourceField.getText().trim());
            double montant = Double.parseDouble(montantField.getText().trim());

            switch (type) {
                case "DEPOT":
                    banqueService.deposer(numeroSource, montant);
                    messageLabel.setText("Dépôt effectué.");
                    break;
                case "RETRAIT":
                    banqueService.retirer(numeroSource, montant);
                    messageLabel.setText("Retrait effectué.");
                    break;
                case "VIREMENT":
                    int numeroDest = Integer.parseInt(compteDestField.getText().trim());
                    banqueService.transferer(numeroSource, numeroDest, montant);
                    messageLabel.setText("Virement effectué.");
                    break;
            }

        } catch (Exception e) {
            messageLabel.setText("Erreur : " + e.getMessage());
        }
    }

    @FXML
    private void backToDashboard() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/ui/AdminDashboard.fxml"));
        Scene scene = new Scene(loader.load());
        AdminDashboardController controller = loader.getController();
        controller.setBanqueService(banqueService);
        controller.setPrimaryStage(primaryStage);
        primaryStage.setScene(scene);
    }
}
