package ui;

import csv.ClientCsvDao;
import csv.CompteCsvDao;
import csv.OperationCsvDao;
// or use your SQL DAOs:
// import sql.ClientSqlDao;
// import sql.CompteSqlDao;
// import sql.OperationSqlDao;
import dao.ClientDao;
import dao.CompteDao;
import dao.OperationDao;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.BanqueService;
import service.BanqueServiceImpl;
import sql.ClientSqlDao;
import sql.CompteSqlDao;

public class MainApp extends Application {

    private BanqueService banqueService;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO: switch to SQL DAOs if you want
        ClientDao clientDao = new ClientSqlDao();
        CompteDao compteDao = new CompteSqlDao();
        OperationDao operationDao = new OperationCsvDao("data/operations.csv");

        banqueService = new BanqueServiceImpl(clientDao, compteDao, operationDao);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/AdminDashboard.fxml"));
        Scene scene = new Scene(loader.load());

        ui.controller.AdminDashboardController controller = loader.getController();
        controller.setBanqueService(banqueService);
        controller.setPrimaryStage(primaryStage);

        primaryStage.setTitle("Système Bancaire - Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
