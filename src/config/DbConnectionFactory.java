package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbConnectionFactory {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://127.0.0.1/banque";
        String user = "root";
        String password = "";

        Connection connection = DriverManager.getConnection(url, user, password);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM clients");

        while (resultSet.next()) {
            String valCol = resultSet.getString("id");
            System.out.println(valCol);
        }

        resultSet.close();
        statement.close();
        connection.close();
    }
}
