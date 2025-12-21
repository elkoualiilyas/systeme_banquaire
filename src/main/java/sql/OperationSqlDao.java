package sql;

import config.DbConnectionFactory;
import dao.OperationDao;
import model.Operation;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OperationSqlDao implements OperationDao {

    private Operation mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        Operation.TypeOperation type = Operation.TypeOperation.valueOf(rs.getString("type"));
        double montant = rs.getDouble("montant");
        Timestamp ts = rs.getTimestamp("date");
        LocalDateTime date = ts.toLocalDateTime();
        int compteSource = rs.getInt("compte_source");
        int compteDestination = rs.getInt("compte_destination");
        Integer dest = rs.wasNull() ? null : compteDestination;

        return new Operation(id, type, montant, date, compteSource, dest);
    }

    @Override
    public Operation save(Operation operation) {
        String sql = "INSERT INTO operations (id, type, montant, date, compte_source, compte_destination) " +
                     "VALUES (?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE type=?, montant=?, date=?, compte_source=?, compte_destination=?";
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, operation.getId());
            ps.setString(2, operation.getType().name());
            ps.setDouble(3, operation.getMontant());
            ps.setTimestamp(4, Timestamp.valueOf(operation.getDate()));
            ps.setInt(5, operation.getCompteSource());
            if (operation.getCompteDestination() == null) {
                ps.setNull(6, Types.INTEGER);
            } else {
                ps.setInt(6, operation.getCompteDestination());
            }

            ps.setString(7, operation.getType().name());
            ps.setDouble(8, operation.getMontant());
            ps.setTimestamp(9, Timestamp.valueOf(operation.getDate()));
            ps.setInt(10, operation.getCompteSource());
            if (operation.getCompteDestination() == null) {
                ps.setNull(11, Types.INTEGER);
            } else {
                ps.setInt(11, operation.getCompteDestination());
            }

            ps.executeUpdate();
            return operation;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL save operation", e);
        }
    }

    @Override
    public List<Operation> findByCompteNumero(int numeroCompte) {
        String sql = "SELECT id, type, montant, date, compte_source, compte_destination " +
                     "FROM operations " +
                     "WHERE compte_source = ? OR compte_destination = ? " +
                     "ORDER BY date DESC";
        List<Operation> list = new ArrayList<>();
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, numeroCompte);
            ps.setInt(2, numeroCompte);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL findByCompteNumero operations", e);
        }
    }

    @Override
    public List<Operation> findAll() {
        String sql = "SELECT id, type, montant, date, compte_source, compte_destination FROM operations";
        List<Operation> list = new ArrayList<>();
        try (Connection conn = DbConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL findAll operations", e);
        }
    }
}
