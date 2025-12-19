package csv;


import dao.OperationDao;
import model.Operation;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OperationCsvDao implements OperationDao {

    private final Path filePath;

    public OperationCsvDao(String filePath) {
        this.filePath = Paths.get(filePath);
        initFile();
    }

    private void initFile() {
        try {
            if (Files.notExists(filePath)) {
                Files.createDirectories(filePath.getParent());
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erreur init operations.csv", e);
        }
    }

    @Override
    public Operation save(Operation operation) {
        List<Operation> all = findAll();
        all.removeIf(o -> o.getId() == operation.getId());
        all.add(operation);
        writeAll(all);
        return operation;
    }

    @Override
    public List<Operation> findByCompteNumero(int numeroCompte) {
        return findAll().stream()
                .filter(o -> o.getCompteSource() == numeroCompte ||
                             (o.getCompteDestination() != null && o.getCompteDestination() == numeroCompte))
                .collect(Collectors.toList());
    }

    @Override
    public List<Operation> findAll() {
        try (Stream<String> lines = Files.lines(filePath)) {
            return lines
                    .filter(l -> !l.isEmpty())
                    .map(this::toOperation)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Erreur lecture operations.csv", e);
        }
    }

    private Operation toOperation(String line) {
        String[] parts = line.split(";");
        String[] p = new String[6];
        for (int i = 0; i < p.length; i++) {
            p[i] = (i < parts.length) ? parts[i] : "";
        }

        int id = Integer.parseInt(p[0]);
        Operation.TypeOperation type = Operation.TypeOperation.valueOf(p[1]);
        double montant = Double.parseDouble(p[2]);
        LocalDateTime date = LocalDateTime.parse(p[3]);
        int compteSource = Integer.parseInt(p[4]);
        Integer compteDestination = p[5].isEmpty() ? null : Integer.parseInt(p[5]);

        return new Operation(id, type, montant, date, compteSource, compteDestination);
    }


    private String fromOperation(Operation o) {
        String dest = (o.getCompteDestination() == null) ? "" : o.getCompteDestination().toString();
        return o.getId() + ";" +
               o.getType().name() + ";" +
               o.getMontant() + ";" +
               o.getDate() + ";" +
               o.getCompteSource() + ";" +
               dest;
    }

    private void writeAll(List<Operation> operations) {
        Path temp = filePath.resolveSibling("operations.tmp");
        try (BufferedWriter writer = Files.newBufferedWriter(temp)) {
            for (Operation o : operations) {
                writer.write(fromOperation(o));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Erreur écriture temp operations", e);
        }
        try {
            Files.move(temp, filePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new RuntimeException("Erreur move temp->operations.csv", e);
        }
    }
}

