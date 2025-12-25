package model;

import java.util.List;

import model.Client;
import model.Compte;
import model.Operation;

public interface BanqueService {

    Client creerClient(String cin, String nom, String prenom);

    Compte creerCompteCourant(int clientId, double soldeInitial);
    Compte creerCompteEpargne(int clientId, double soldeInitial);

    double consulterSolde(int numeroCompte);

    void deposer(int numeroCompte, double montant);

    void retirer(int numeroCompte, double montant);

    void transferer(int numeroCompteSource, int numeroCompteDestination, double montant);
    List<Client> listerClients();

    List<Compte> listerComptes();              
    List<Compte> listerComptesParClient(int id);
    List<Operation> historiqueOperations(int numeroCompte);
    
}