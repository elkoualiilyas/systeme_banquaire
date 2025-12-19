package csv;


import dao.ClientDao;
import model.Client;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientCsvDao implements ClientDao {

    private final Path filePath;

    public ClientCsvDao(String filePath) {
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
            throw new RuntimeException("Erreur init clients.csv", e);
        }
    }

    @Override
    public Client save(Client client) {
        List<Client> all = findAll();
        all.removeIf(c -> c.getId() == client.getId());
        all.add(client);
        writeAll(all);
        return client;
    }

    @Override
    public Optional<Client> findById(int id) {
        return findAll().stream()
                .filter(c -> c.getId() == id)
                .findFirst();
    }

    @Override
    public Optional<Client> findByCin(String cin) {
        return findAll().stream()
                .filter(c -> c.getCin().equalsIgnoreCase(cin))
                .findFirst();
    }

    @Override
    public List<Client> findAll() {
        try (Stream<String> lines = Files.lines(filePath)) {
            return lines
                    .filter(l -> !l.isEmpty())
                    .map(this::toClient)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Erreur lecture clients.csv", e);
        }
    }

    @Override
    public void deleteById(int id) {
        List<Client> all = findAll();
        all.removeIf(c -> c.getId() == id);
        writeAll(all);
    }

    private Client toClient(String line) {
        String[] parts = line.split(";");
        int id = Integer.parseInt(parts[0]);
        String cin = parts[1];
        String nom = parts[2];
        String prenom = parts[3];
        return new Client(id, cin, nom, prenom);
    }

    private String fromClient(Client c) {
        return c.getId() + ";" +
               c.getCin() + ";" +
               c.getNom() + ";" +
               c.getPrenom();
    }

    private void writeAll(List<Client> clients) {
        Path temp = filePath.resolveSibling("clients.tmp");
        try (BufferedWriter writer = Files.newBufferedWriter(temp)) {
            for (Client c : clients) {
                writer.write(fromClient(c));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Erreur écriture temp clients", e);
        }
        try {
            Files.move(temp, filePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new RuntimeException("Erreur move temp->clients.csv", e);
        }
    }
}

