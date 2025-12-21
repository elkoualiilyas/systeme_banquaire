package model;

import dao.ClientDao;
import dao.CompteDao;
import dao.OperationDao;
import service.BanqueServiceImpl;
import sql.ClientSqlDao;
import sql.CompteSqlDao;
import sql.OperationSqlDao;
import model.Operation;

public class MainSqlTest {

    public static void main(String[] args) {
        // 1) Use SQL DAOs
        ClientDao clientDao = new ClientSqlDao();
        CompteDao compteDao = new CompteSqlDao();
        OperationDao operationDao = new OperationSqlDao();

        // 2) Service with multithreading + SQL
        BanqueService service = new BanqueServiceImpl(clientDao, compteDao, operationDao);

        // 3) Scenario de test
        var c1 = service.creerClient("AA111", "ALAMI", "Youssef");
        var c2 = service.creerClient("BB222", "NASSER", "Sara");

        var compte1 = service.creerCompteCourant(c1.getId(), 1000);
        var compte2 = service.creerCompteEpargne(c2.getId(), 500);

        System.out.println("Solde compte1 initial = " + service.consulterSolde(compte1.getNumero()));
        System.out.println("Solde compte2 initial = " + service.consulterSolde(compte2.getNumero()));

        service.deposer(compte1.getNumero(), 200);
        service.retirer(compte2.getNumero(), 100);
        service.transferer(compte1.getNumero(), compte2.getNumero(), 300);

        System.out.println("Solde compte1 final = " + service.consulterSolde(compte1.getNumero()));
        System.out.println("Solde compte2 final = " + service.consulterSolde(compte2.getNumero()));

        System.out.println("Historique opérations compte1 :");
        for (Operation op : service.historiqueOperations(compte1.getNumero())) {
            System.out.println(op.getId() + " - " + op.getType() + " - " + op.getMontant());
        }

        System.out.println("Test SQL terminé.");
    }
}
