package modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db {

    private static String url = "jdbc:mysql://localhost:3306/biblioteca";
    private static String username = "root";
    private static String password = "root";

    public Db() {}

    public Db(String url, String username, String password) {
        Db.url = url;
        Db.username = username;
        Db.password = password;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
