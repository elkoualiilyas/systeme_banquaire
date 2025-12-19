package csv;


import dao.CompteDao;
import model.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CompteCsvDao implements CompteDao {

    private final Path filePath;

    public CompteCsvDao(String filePath) {
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
            throw new RuntimeException("Erreur init comptes.csv", e);
        }
    }

    @Override
    public Compte save(Compte compte) {
        List<Compte> all = findAll();
        all.removeIf(c -> c.getNumero() == compte.getNumero());
        all.add(compte);
        writeAll(all);
        return compte;
    }

    @Override
    public Optional<Compte> findByNumero(int numero) {
        return findAll().stream()
                .filter(c -> c.getNumero() == numero)
                .findFirst();
    }

    @Override
    public List<Compte> findByClientId(int clientId) {
        return findAll().stream()
                .filter(c -> c.getClientId() == clientId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Compte> findAll() {
        try (Stream<String> lines = Files.lines(filePath)) {
            return lines
                    .filter(l -> !l.isEmpty())
                    .map(this::toCompte)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Erreur lecture comptes.csv", e);
        }
    }

    @Override
    public void deleteByNumero(int numero) {
        List<Compte> all = findAll();
        all.removeIf(c -> c.getNumero() == numero);
        writeAll(all);
    }

    private Compte toCompte(String line) {
        String[] parts = line.split(";");
        int numero = Integer.parseInt(parts[0]);
        double solde = Double.parseDouble(parts[1]);
        int clientId = Integer.parseInt(parts[2]);
        String type = parts[3];

        if ("COURANT".equalsIgnoreCase(type)) {
            return new CompteCourant(numero, solde, clientId);
        } else {
            return new CompteEpargne(numero, solde, clientId);
        }
    }

    private String fromCompte(Compte c) {
        String type = (c instanceof CompteCourant) ? "COURANT" : "EPARGNE";
        return c.getNumero() + ";" +
               c.getSolde() + ";" +
               c.getClientId() + ";" +
               type;
    }

    private void writeAll(List<Compte> comptes) {
        Path temp = filePath.resolveSibling("comptes.tmp");
        try (BufferedWriter writer = Files.newBufferedWriter(temp)) {
            for (Compte c : comptes) {
                writer.write(fromCompte(c));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Erreur écriture temp comptes", e);
        }
        try {
            Files.move(temp, filePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new RuntimeException("Erreur move temp->comptes.csv", e);
        }
    }
}
