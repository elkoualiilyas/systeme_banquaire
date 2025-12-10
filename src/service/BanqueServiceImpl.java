package service;

import dao.ClientDao;
import dao.CompteDao;
import dao.OperationDao;
import model.*;

import java.time.LocalDateTime;
import java.util.List;

public class BanqueServiceImpl implements BanqueService {

    private final ClientDao clientDao;
    private final CompteDao compteDao;
    private final OperationDao operationDao;

    private int nextClientId = 1;
    private int nextCompteNumero = 1;
    private int nextOperationId = 1;

    public BanqueServiceImpl(ClientDao clientDao,
                             CompteDao compteDao,
                             OperationDao operationDao) {
        this.clientDao = clientDao;
        this.compteDao = compteDao;
        this.operationDao = operationDao;
    }

    @Override
    public Client creerClient(String cin, String nom, String prenom) {
        Client client = new Client(nextClientId++, cin, nom, prenom);
        return clientDao.save(client);
    }

    @Override
    public Compte creerCompteCourant(int clientId, double soldeInitial) {
        Compte compte = new CompteCourant(nextCompteNumero++, soldeInitial, clientId);
        return compteDao.save(compte);
    }

    @Override
    public Compte creerCompteEpargne(int clientId, double soldeInitial) {
        Compte compte = new CompteEpargne(nextCompteNumero++, soldeInitial, clientId);
        return compteDao.save(compte);
    }

    @Override
    public double consulterSolde(int numeroCompte) {
        Compte c = compteDao.findByNumero(numeroCompte)
                .orElseThrow(() -> new RuntimeException("Compte introuvable"));
        return c.getSolde();
    }

    @Override
    public void deposer(int numeroCompte, double montant) {
        Compte c = compteDao.findByNumero(numeroCompte)
                .orElseThrow(() -> new RuntimeException("Compte introuvable"));
        c.deposer(montant);
        compteDao.save(c);
        enregistrerOperation(Operation.TypeOperation.DEPOT, montant, numeroCompte, null);
    }

    @Override
    public void retirer(int numeroCompte, double montant) {
        Compte c = compteDao.findByNumero(numeroCompte)
                .orElseThrow(() -> new RuntimeException("Compte introuvable"));
        c.retirer(montant);
        compteDao.save(c);
        enregistrerOperation(Operation.TypeOperation.RETRAIT, montant, numeroCompte, null);
    }

    @Override
    public void transferer(int numeroCompteSource, int numeroCompteDestination, double montant) {
        Compte source = compteDao.findByNumero(numeroCompteSource)
                .orElseThrow(() -> new RuntimeException("Source introuvable"));
        Compte dest = compteDao.findByNumero(numeroCompteDestination)
                .orElseThrow(() -> new RuntimeException("Destination introuvable"));

        source.transferer(dest, montant);
        compteDao.save(source);
        compteDao.save(dest);
        enregistrerOperation(Operation.TypeOperation.VIREMENT, montant,
                numeroCompteSource, numeroCompteDestination);
    }

    @Override
    public List<Operation> historiqueOperations(int numeroCompte) {
        return operationDao.findByCompteNumero(numeroCompte);
    }

    private Operation enregistrerOperation(Operation.TypeOperation type,
                                           double montant,
                                           int numeroSource,
                                           Integer numeroDestination) {
        Operation op = new Operation(
                nextOperationId++,
                type,
                montant,
                LocalDateTime.now(),
                numeroSource,
                numeroDestination
        );
        return operationDao.save(op);
    }
}
