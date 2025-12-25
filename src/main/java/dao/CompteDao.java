package dao;

import model.Compte;
import java.util.List;
import java.util.Optional;

public interface CompteDao {

    Compte save(Compte compte);          // create/update
    Optional<Compte> findByNumero(int numero);
    List<Compte> findByClientId(int clientId);
    List<Compte> findAll();
    void deleteByNumero(int numero);
 
}
