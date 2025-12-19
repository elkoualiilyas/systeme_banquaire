package sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import config.DbConnectionFactory;

public class TestDbConnection {

    public static void main(String[] args) {
        try (Connection connection = DbConnectionFactory.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM clients")) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                System.out.println("Client id = " + id);
            }

            System.out.println("Test terminé avec succès.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
