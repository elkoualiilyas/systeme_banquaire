package sql;

import config.DbConnectionFactory;
import dao.CompteDao;
import model.Compte;
import model.CompteCourant;
import model.CompteEpargne;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompteSqlDao implements CompteDao {

    private Compte mapRow(ResultSet rs) throws SQLException {
        int numero = rs.getInt("numero");
        double solde = rs.getDouble("solde");
        int clientId = rs.getInt("client_id");
        String type = rs.getString("type_compte");

        if ("COURANT".equalsIgnoreCase(type)) {
            return new CompteCourant(numero, solde, clientId);
        } else {
            return new CompteEpargne(numero, solde, clientId);
        }
    }

    @Override
    public Compte save(Compte compte) {
        String sql = "INSERT INTO comptes (numero, solde, client_id, type_compte) " +
                     "VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE solde=?, client_id=?, type_compte=?";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String type = (compte instanceof CompteCourant) ? "COURANT" : "EPARGNE";

            ps.setInt(1, compte.getNumero());
            ps.setDouble(2, compte.getSolde());
            ps.setInt(3, compte.getClientId());
            ps.setString(4, type);

            ps.setDouble(5, compte.getSolde());
            ps.setInt(6, compte.getClientId());
            ps.setString(7, type);

            ps.executeUpdate();
            return compte;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL save compte", e);
        }
    }

    @Override
    public Optional<Compte> findByNumero(int numero) {
        String sql = "SELECT numero, solde, client_id, type_compte FROM comptes WHERE numero=?";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, numero);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL findByNumero compte", e);
        }
    }

    @Override
    public List<Compte> findByClientId(int clientId) {
        String sql = "SELECT numero, solde, client_id, type_compte FROM comptes WHERE client_id=?";
        List<Compte> list = new ArrayList<>();
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL findByClientId compte", e);
        }
    }

    @Override
    public List<Compte> findAll() {
        String sql = "SELECT numero, solde, client_id, type_compte FROM comptes";
        List<Compte> list = new ArrayList<>();
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL findAll comptes", e);
        }
    }

    @Override
    public void deleteByNumero(int numero) {
        String sql = "DELETE FROM comptes WHERE numero=?";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, numero);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL deleteByNumero compte", e);
        }
    }
}
