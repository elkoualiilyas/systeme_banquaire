package dao;

import model.Operation;
import java.util.List;

public interface OperationDao {

    Operation save(Operation operation);
    List<Operation> findByCompteNumero(int numeroCompte);
    List<Operation> findAll();
}