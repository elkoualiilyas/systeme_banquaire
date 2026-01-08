package service;

import dao.ClientDao;
import dao.CompteDao;
import dao.OperationDao;
import model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

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
    private final ConcurrentHashMap<Integer, ReentrantLock> verrous = new ConcurrentHashMap<>();
    private ReentrantLock getLock(int numeroCompte) {
    	return verrous.computeIfAbsent(numeroCompte, n->new ReentrantLock());
    }

    @Override
    public Client creerClient(String cin, String nom, String prenom) {
        Client client = new Client(nextClientId++, cin, nom, prenom);
        return clientDao.save(client);
    }

    @Override
    public Compte creerCompteCourant(int clientId, double soldeInitial) {
        // 1 courant max
        boolean hasCourant = compteDao.findByClientId(clientId).stream()
                .anyMatch(c -> c instanceof CompteCourant);
        if (hasCourant) {
            throw new RuntimeException("Ce client a déjà un compte courant.");
        }

        Compte compte = new CompteCourant(nextCompteNumero++, soldeInitial, clientId);
        return compteDao.save(compte);
    }

    @Override
    public Compte creerCompteEpargne(int clientId, double soldeInitial) {
        // 1 épargne max
        boolean hasEpargne = compteDao.findByClientId(clientId).stream()
                .anyMatch(c -> c instanceof CompteEpargne);
        if (hasEpargne) {
            throw new RuntimeException("Ce client a déjà un compte épargne.");
        }

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
    	ReentrantLock depLock=getLock(numeroCompte);
    	depLock.lock();
    	try {
        Compte c = compteDao.findByNumero(numeroCompte)
                .orElseThrow(() -> new RuntimeException("Compte introuvable"));
        c.deposer(montant);
        compteDao.save(c);
        enregistrerOperation(Operation.TypeOperation.DEPOT, montant, numeroCompte, null);
    	}finally {
    		depLock.unlock();
    	}
    }

    @Override
    public void retirer(int numeroCompte, double montant) {
    	ReentrantLock reLock=getLock(numeroCompte);
    	reLock.lock();
    	try {
        Compte c = compteDao.findByNumero(numeroCompte)
                .orElseThrow(() -> new RuntimeException("Compte introuvable"));
        c.retirer(montant);
        compteDao.save(c);
        enregistrerOperation(Operation.TypeOperation.RETRAIT, montant, numeroCompte, null);
    	}finally {
    		reLock.unlock();
    	}
    }
    @Override
    public List<Client> listerClients() {
        return clientDao.findAll(); // will read from CSV or SQL depending on the DAO you injected
    }

    @Override
    public void transferer(int numeroCompteSource, int numeroCompteDestination, double montant) {
    	ReentrantLock srcLock=getLock(numeroCompteSource);
    	ReentrantLock dstLock=getLock(numeroCompteDestination);
    	ReentrantLock first = (numeroCompteSource < numeroCompteDestination) ? srcLock : dstLock;
        ReentrantLock second = (numeroCompteSource < numeroCompteDestination) ? dstLock : srcLock;
    	first.lock();
    	second.lock();
    	try { 
    		Compte source = compteDao.findByNumero(numeroCompteSource)
                .orElseThrow(() -> new RuntimeException("Source introuvable"));
        Compte dest = compteDao.findByNumero(numeroCompteDestination)
                .orElseThrow(() -> new RuntimeException("Destination introuvable"));

        source.transferer(dest, montant);
        compteDao.save(source);
        compteDao.save(dest);
        enregistrerOperation(Operation.TypeOperation.VIREMENT, montant,
                numeroCompteSource, numeroCompteDestination);
        }finally {
        	first.unlock();
        	second.unlock();
        }
      
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
    @Override
    public List<Compte> listerComptes() {
        return compteDao.findAll();
    }

    @Override
    public List<Compte> listerComptesParClient(int clientId) {
        return compteDao.findByClientId(clientId);
    }
    @Override
    public void modifierClient(Client client) {
        clientDao.save(client); // DAO handles update/insert
    }

    @Override
    public void supprimerClient(int clientId) {
        // Cascade delete comptes first
        List<Compte> comptes = compteDao.findByClientId(clientId);
        for (Compte c : comptes) {
            compteDao.deleteByNumero(c.getNumero());
        }
        
        // Delete client
        clientDao.deleteById(clientId);
    }
}
