package model;
import csv.ClientCsvDao;
import csv.CompteCsvDao;
import csv.OperationCsvDao;
import model.BanqueService;
import service.BanqueServiceImpl;
import model.Operation;

public class Main {
    public static void main(String[] args) {
        ClientCsvDao clientDao = new ClientCsvDao("data/clients.csv");
        CompteCsvDao compteDao = new CompteCsvDao("data/comptes.csv");
        OperationCsvDao operationDao = new OperationCsvDao("data/operations.csv");

        BanqueService service = new BanqueServiceImpl(clientDao, compteDao, operationDao);

        // 1) Créer deux clients
        var c1 = service.creerClient("AA111", "ALAMI", "Youssef");
        var c2 = service.creerClient("BB222", "NASSER", "Sara");

        // 2) Créer un compte pour chaque client
        var compte1 = service.creerCompteCourant(c1.getId(), 1000);
        var compte2 = service.creerCompteEpargne(c2.getId(), 500);

        System.out.println("Solde compte1 initial = " + service.consulterSolde(compte1.getNumero()));
        System.out.println("Solde compte2 initial = " + service.consulterSolde(compte2.getNumero()));

        // 3) Dépôt sur compte1
        service.deposer(compte1.getNumero(), 200);
        System.out.println("Solde compte1 après dépôt 200 = " + service.consulterSolde(compte1.getNumero()));

       service.deposer(compte1.getNumero(), 4000);
       System.out.println("virement effectue solde de compte: "+compte1.getClientId()+" est: "+service.consulterSolde(compte1.getNumero()));
       // 5) Virement de compte1 vers compte2
       service.transferer(compte1.getNumero(), compte2.getNumero(), 300);
       System.out.println("Solde compte1 après virement 300 = " + service.consulterSolde(compte1.getNumero()));
       System.out.println("Solde compte2 après virement 300 = " + service.consulterSolde(compte2.getNumero()));
    }
}
