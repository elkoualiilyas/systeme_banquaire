package dao;

import model.Client;
import java.util.List;
import java.util.Optional;

public interface ClientDao {

    Client save(Client client);           // create/update
    Optional<Client> findById(int id);
    Optional<Client> findByCin(String cin);
    List<Client> findAll();
    void deleteById(int id);
}
