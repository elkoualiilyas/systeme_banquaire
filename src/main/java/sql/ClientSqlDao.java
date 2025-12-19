
package sql;

import config.DbConnectionFactory;
import dao.ClientDao;
import model.Client;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientSqlDao implements ClientDao {

    @Override
    public Client save(Client client) {
        String insert = "INSERT INTO clients (id, cin, nom, prenom) VALUES (?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE cin=?, nom=?, prenom=?";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(insert)) {

            ps.setInt(1, client.getId());
            ps.setString(2, client.getCin());
            ps.setString(3, client.getNom());
            ps.setString(4, client.getPrenom());
            ps.setString(5, client.getCin());
            ps.setString(6, client.getNom());
            ps.setString(7, client.getPrenom());
            ps.executeUpdate();
            return client;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL save client", e);
        }
    }

    @Override
    public Optional<Client> findById(int id) {
        String sql = "SELECT id, cin, nom, prenom FROM clients WHERE id=?";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Client c = new Client(
                            rs.getInt("id"),
                            rs.getString("cin"),
                            rs.getString("nom"),
                            rs.getString("prenom"));
                    return Optional.of(c);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL findById client", e);
        }
    }

    @Override
    public Optional<Client> findByCin(String cin) {
        String sql = "SELECT id, cin, nom, prenom FROM clients WHERE cin=?";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cin);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Client c = new Client(
                            rs.getInt("id"),
                            rs.getString("cin"),
                            rs.getString("nom"),
                            rs.getString("prenom"));
                    return Optional.of(c);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL findByCin client", e);
        }
    }

    @Override
    public List<Client> findAll() {
        String sql = "SELECT id, cin, nom, prenom FROM clients";
        List<Client> list = new ArrayList<>();
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Client(
                        rs.getInt("id"),
                        rs.getString("cin"),
                        rs.getString("nom"),
                        rs.getString("prenom")));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL findAll clients", e);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM clients WHERE id=?";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL deleteById client", e);
        }
    }
}

